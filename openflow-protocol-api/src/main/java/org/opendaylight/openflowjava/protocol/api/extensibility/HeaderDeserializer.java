/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * @param <E> 
 */
public interface HeaderDeserializer<E extends DataObject> extends OFGeneralDeserializer {

    /**
     * Deserializes byte message headers
     * 
     * @param rawMessage message as bytes in ByteBuf
     * @return POJO/DTO
     */
    public E deserializeHeader(ByteBuf rawMessage);
}
