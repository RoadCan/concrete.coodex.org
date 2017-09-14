// 定义抽象ConcreteService的行为, Concrete公用单元

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import 'rxjs/add/observable/dom/webSocket';
import {Subject} from 'rxjs/Subject';
import {WebSocketSubjectConfig} from 'rxjs/observable/dom/WebSocketSubject';
import {Observer} from 'rxjs/Observer';
import {isUndefined} from 'util';

// TODO: change it
const url: string = 'ws://localhost:8080/WebSocket';

class WebSocketService {
    private websocket: Subject<any>;
    private bcSubject: Map<string, Subject<any>> = new Map();
    private map: Map<string, any> = new Map();

    constructor(urlConfigOrSource: string | WebSocketSubjectConfig) {
        this.websocket = Observable.webSocket(urlConfigOrSource);

        this.websocket.subscribe((value) => this.onMessage(value),
            () => {
            },
            () => {
            });

    }

    private generateUUID(): string {
        let d = new Date().getTime();
        const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c === 'x' ? r : (r & 0x7 | 0x8)).toString(16);
        });
        return uuid;
    }

    private put(msgId: string, observer: Observer<any>) {
        const finalMap = this.map;
        finalMap.set(msgId, {
            'future': setTimeout(function () {
                observer.error({
                    clientSide: true,
                    code: 99998,
                    msg: 'timeOut',
                });
                finalMap.delete(msgId);
            }, 15 * 60 * 1000),
            'observer': observer,
        });
    }

    private pick(msgId: string): Observer<any> {
        const p = this.map.get(msgId);
        if (p) {
            clearTimeout(p.future);
            return p.observer;
        }
        return null;
    }

    private onMessage(responsePackage: any): void {
        const msgId = responsePackage.msgId;
        if (!msgId) {
            console.warn('invalid msg: ' + JSON.stringify(responsePackage));
            return;
        }

        if (responsePackage.subjoin && responsePackage.subjoin.broadcast) {
            this.bcSubject.get(responsePackage.subjoin.subject).next(responsePackage.content);
        } else {
            responsePackage.ok ? this.onReturn(msgId, responsePackage.content) : this.onError(msgId, responsePackage.content);
        }
    }

    private onReturn(msgId: string, content: any): void {
        const observer: Observer<any> = this.pick(msgId);
        try {
            if (content !== null && !isUndefined(content)) {
                observer.next(content);
            }
            observer.complete();
        } catch (e) {
            observer.error({
                clientSide: true,
                code: 99997,
                msg: e.toLocaleString(),
            });
        }
    }

    private onError(msgId: string, content: any): void {
        this.pick(msgId).error(content);
    }


    send(serviceId: string, data: any): Observable<any> {
        const msgId: string = this.generateUUID();

        this.websocket.next(JSON.stringify({
            'msgId': msgId,
            'serviceId': serviceId,
            'content': data,
        }));

        const self: WebSocketService = this;
        return Observable.create(obs => self.put(msgId, obs));
    }

    subscribe(subject: string, observer: Observer<any>) {
        if (!this.bcSubject.get(subject)) {
            this.bcSubject.set(subject, new Subject());
        }
        this.bcSubject.get(subject).subscribe(observer);
    }
}

const webSocketService: WebSocketService = new WebSocketService(url);


export class Broadcast {
    public static subscribe(subject: string, observer: Observer<any>): void {
        webSocketService.subscribe(subject, observer);
    }
}

export abstract class AbstractConcreteService {


    protected send(serviceId: string, data: any): Observable<any> {
        return webSocketService.send(serviceId, data);
    }

}