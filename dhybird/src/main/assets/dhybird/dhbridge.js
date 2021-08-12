(function () {

    let ua = navigator.userAgent;
    let uniqueId = 1;
    let callbacks = {};

    //js给native发消息
    function _doSend(message) {
        if ((ua.indexOf("iPhone") > 0) || ua.indexOf("iPad") > 0) {
            return window.webkit.messageHandlers.DahaiHybridWK.postMessage(JSON.stringify(message));
        } else if (ua.indexOf("Android") > 0) {
            if (window.android) {
                return android.invoke(JSON.stringify(message));
            }
        }
    }

    //包装js给native发消息的数据格式
    function send(message, callback) {
        if (callback) {
            let callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            callbacks[callbackId] = callback;
            message.callbackId = callbackId;
        }
        return _doSend(message);
    }

    //包装native主动通知js的消息的数据格式
    //注意，这种情况不需要给native发消息，只需要在js侧注册
    function onListenEvent(eventName, eventCallback) {
        if (!eventName || !eventCallback) {
            return
        }
        var eventList = [];
        if (callbacks[eventName]) {
            eventList = callbacks[eventName];
        }
        eventList.push(eventCallback);
        callbacks[eventName] = eventList;
    }

    //native返回的消息处理
    function _dispatchMessageFromNative(messageObj) {
        let callback;
        if (messageObj.callbackId) {
            callback = callbacks[messageObj.callbackId];
            if (!callback) {
                return;
            }
            callback(messageObj);
            if (messageObj.complete !== 0) {
                delete callbacks[messageObj.callbackId];
            }
        }
    }

    //接收native返回的消息
    function handleMessageFromNative(message) {
        //控制台打印返回值
        console.log(message);
        let messageObj = toJson(message);
        _dispatchMessageFromNative(messageObj);
    }

    //native主动通知js的消息处理
    function _dispatchListenEventFromNative(messageObj) {
        if (messageObj.callbackId) {
            var eventList = callbacks[messageObj.callbackId];
            eventList.forEach((eventCallback, index, array) => {
                eventCallback(messageObj);
            });
        }
    }

    //接收native主动通知js的消息
    function handleListenEventFromNative(message) {
        //控制台打印返回值
        console.log(message)
        let messageObj = toJson(message);
        _dispatchListenEventFromNative(messageObj);
    }

    //native传来的message统一转成obj对象
    function toJson(obj) {
        if (typeof (obj) == 'string') {
            return JSON.parse(obj);
        } else {
            return obj;
        }
    }

    //dhsdk.js方法调用
    window._dhbridge = {
        invoke: function (message, callback) {
            return send(message, callback)
        },
        onListenEvent: function (eventName, eventCallback) {
            return onListenEvent(eventName, eventCallback)
        }
    };
    window.handleMessageFromNative = handleMessageFromNative;
    window.handleListenEventFromNative = handleListenEventFromNative;

    //dhbridge.js准备就绪逻辑
    var doc = document;
    var readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('DHBridgeReady');
    readyEvent.bridge = window._dhbridge;
    doc.dispatchEvent(readyEvent);

})();