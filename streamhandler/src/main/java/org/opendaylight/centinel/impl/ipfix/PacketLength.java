/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

public abstract class PacketLength {

    protected int length;

    protected void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

}
