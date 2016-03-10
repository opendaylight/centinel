/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.Calendar;

public class ResetCounterThreadFactory {

    public WidgetStreamCounterVO setValuesForSaveInDb() {
        WidgetStreamCounterVO mockWidgetStreamCounterVO = new WidgetStreamCounterVO();
        mockWidgetStreamCounterVO.setWidgetID("1001");
        mockWidgetStreamCounterVO.setCounter(1);
        mockWidgetStreamCounterVO.setCal(Calendar.getInstance());
        return mockWidgetStreamCounterVO;
    }

}
