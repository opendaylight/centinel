/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

public class LogCollectorFactory {

    String logMessage;

    public String inputStringMessageBadTimeFormat() {
        logMessage = "<77>1 Jan 27 2015 12:00:00 tcs-ThinkCentre-M93p anacron 7245 - -  Job `cron.daily' terminated";
        return logMessage;
    }

    public String inputStringMessageNoTimeStamp() {
        logMessage = "<7>-administrator-ThinkPad-L450 anacron[924]: Job `cron.weekly' terminated";
        return logMessage;
    }

    public String inputStringMessageCorrectParse() {
        logMessage = "<7>Jan 27 11:55:16 administrator-ThinkPad-L450 anacron[924]: Job `cron.weekly' terminated";
        return logMessage;
    }

    public String inputStringMessagerfc5424() {
        logMessage = "<165>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog - ID47 [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"] BOMAn application event log entry...";
        return logMessage;
    }
}
