/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yangtools.yang.binding.DataObject;

import io.netty.buffer.ByteBuf;

/**
 * Uniform interface for serializers
 * @author michal.polkorab
 * @author timotej.kubas
 * @param <SERIALIZER_TYPE> message type
 */
public interface OFSerializer <SERIALIZER_TYPE extends DataObject> extends OFGeneralSerializer {

    /**
     * Transforms POJO/DTO into byte message (ByteBuf).
     * @param input object to be serialized
     * @param outBuffer output buffer
     */
    public void serialize(SERIALIZER_TYPE input, ByteBuf outBuffer);

}
