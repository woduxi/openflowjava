/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactory implements OFSerializer<HelloInput>{

    /** Code type of Hello message */
    public static final byte MESSAGE_TYPE = 0;
    private static final int MESSAGE_LENGTH = 8;
    private static HelloInputMessageFactory instance;
    
    private HelloInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static HelloInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new HelloInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out, HelloInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        // TODO - fill list of elements into ByteBuf, check length too
    }

    @Override
    public int computeLength() {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
 
}
