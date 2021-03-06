/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler.COMPONENT_NAMES;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the channel
 * @author michal.polkorab
 */
public class PublishingChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublishingChannelInitializer.class);
    private DefaultChannelGroup allChannels;
    private SwitchConnectionHandler switchConnectionHandler;
    private long switchIdleTimeout;
    private boolean encryption;
    private SerializationFactory serializationFactory;
    private DeserializationFactory deserializationFactory;

    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        allChannels = new DefaultChannelGroup("netty-receiver", null);
    }
    
    @Override
    protected void initChannel(SocketChannel ch) {
        InetAddress switchAddress = ch.remoteAddress().getAddress();
        int port = ch.localAddress().getPort();
        int remotePort = ch.remoteAddress().getPort();
        LOGGER.info("Incoming connection from (remote address): " + switchAddress.toString()
                + ":" + remotePort + " --> :" + port);
        if (!switchConnectionHandler.accept(switchAddress)) {
            ch.disconnect();
            LOGGER.info("Incoming connection rejected");
            return;
        }
        LOGGER.info("Incoming connection accepted - building pipeline");
        allChannels.add(ch);
        ConnectionFacade connectionFacade = null;
        connectionFacade = ConnectionAdapterFactory.createConnectionFacade(ch);
        try {
            LOGGER.debug("calling plugin: "+switchConnectionHandler);
            switchConnectionHandler.onSwitchConnected(connectionFacade);
            connectionFacade.checkListeners();
            TlsDetector tlsDetector;
            ch.pipeline().addLast(COMPONENT_NAMES.IDLE_HANDLER.name(), new IdleHandler(switchIdleTimeout, 0, 0, TimeUnit.MILLISECONDS));
            if (encryption) {
                tlsDetector =  new TlsDetector();
                tlsDetector.setConnectionFacade(connectionFacade);
                ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), tlsDetector);
            }
            ch.pipeline().addLast(COMPONENT_NAMES.OF_FRAME_DECODER.name(), new OFFrameDecoder());
            ch.pipeline().addLast(COMPONENT_NAMES.OF_VERSION_DETECTOR.name(), new OFVersionDetector());
            OFDecoder ofDecoder = new OFDecoder();
            ofDecoder.setDeserializationFactory(deserializationFactory);
            ch.pipeline().addLast(COMPONENT_NAMES.OF_DECODER.name(), ofDecoder);
            OFEncoder ofEncoder = new OFEncoder();
            ofEncoder.setSerializationFactory(serializationFactory);
            ch.pipeline().addLast(COMPONENT_NAMES.OF_ENCODER.name(), ofEncoder);
            ch.pipeline().addLast(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER.name(), new DelegatingInboundHandler(connectionFacade));
            if (!encryption) {
                connectionFacade.fireConnectionReadyNotification();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ch.close();
        }
    }
    
    /**
     * @return iterator through active connections
     */
    public Iterator<Channel> getConnectionIterator() {
        return allChannels.iterator();
    }

    /**
     * @return amount of active channels
     */
    public int size() {
        return allChannels.size();
    }
    
    /**
     * @param switchConnectionHandler the switchConnectionHandler to set
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }

    /**
     * @param switchIdleTimeout the switchIdleTimeout to set
     */
    public void setSwitchIdleTimeout(long switchIdleTimeout) {
        this.switchIdleTimeout = switchIdleTimeout;
    }

    /**
     * @param tlsSupported
     */
    public void setEncryption(boolean tlsSupported) {
        encryption = tlsSupported;
    }

    /**
     * @param serializationFactory
     */
    public void setSerializationFactory(SerializationFactory serializationFactory) {
        this.serializationFactory = serializationFactory;
    }
    
    /**
     * @param deserializationFactory
     */
    public void setDeserializationFactory(DeserializationFactory deserializationFactory) {
        this.deserializationFactory = deserializationFactory;
    }

}
