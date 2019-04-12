/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import {dateTimeWithoutTimeZone, newFlowId, tcEscape} from "./utils"

export type TeamCityMessageData = { [key: string]: any }

export class TeamCityMessagesFlow {
    public readonly id: number;

    constructor(id: number | null, private readonly send: (payload: string) => void) {
        this.id = id || newFlowId()
    }

    sendMessage(type: string, args: TeamCityMessageData) {
        args['flowId'] = this.id;
        args['timestamp'] = dateTimeWithoutTimeZone();

        const serializedArgs = Object
            .keys(args)
            .map((key) => `${key}='${tcEscape(args[key])}'`)
            .join(' ');

        this.send(`##teamcity[${type} ${serializedArgs}]`)
    }
}