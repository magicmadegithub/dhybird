(function () {
  let version = '1.0.1';

  function onListenEvent(eventName, eventCallback) {
    _dhbridge.onListenEvent(eventName, eventCallback);
  }

  // dhsdk准备就绪回调
  function ready(callback) {
    if (window._dhbridge) {
      callback(window._dhbridge);
    } else {
      document.addEventListener(
        'DHBridgeReady',
        function () {
          callback(window._dhbridge);
        },
        false
      );
    }
  }

  function showToast(args) {
    let data = {
      message: args.message,
    };

    let message = {
      data: data,
      plugin: nativeApi.showToast,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  function getImage(args) {
    let message = {
      data: args.params,
      plugin: nativeApi.getImage,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  function request(args) {
    let data = {
      url: args.url,
      method: args.method,
      data: args.data,
      header: args.header,
    };

    let message = {
      data: data,
      plugin: nativeApi.request,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // dhsdk获取getToken
  function getToken(args) {
    let message = {
      data: '',
      plugin: nativeApi.getToken,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // dhsdk获取getToken并且返回promise
  function getTokenPromise() {
    let message = {
      data: '',
      plugin: nativeApi.getToken,
      sdkVersion: version,
    };

    return promisify(message);
  }

  // 获取手机号
  function getMobile(args) {
    let message = {
      data: args,
      plugin: nativeApi.getMobile,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 获取userid
  function getUid(args) {
    let message = {
      data: '',
      plugin: nativeApi.getUid,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 分享
  function share(args) {
    // let data = {}

    let message = {
      data: args,
      plugin: nativeApi.share,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 跳转到登录页面
  function login(args) {
    let message = {
      data: args,
      plugin: nativeApi.login,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 跳转到登陆页面并且返回promise数据
  function loginPromise(args) {
    let message = {
      data: args,
      plugin: nativeApi.login,
      sdkVersion: version,
    };

    return promisify(message);
  }

  // 跳转到提问界面
  function question(args) {
    let message = {
      data: args,
      plugin: nativeApi.question,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 全屏播放
  function playVideo(args) {
    let message = {
      data: args,
      plugin: nativeApi.playVideo,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 设置导航栏颜色
  function setColor(args) {
    let message = {
      data: args,
      plugin: nativeApi.setColor,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 设置标题
  function setTitle(args) {
    let message = {
      data: args,
      plugin: nativeApi.setTitle,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 关闭浏览器
  function closeHybird(args) {
    let message = {
      data: args,
      plugin: nativeApi.closeHybird,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 调起拨打电话
  function call(args) {
    let message = {
      data: args,
      plugin: nativeApi.call,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // 一键预约成功之后通知navtive
  function appointExpert(args) {
    let message = {
      data: args,
      plugin: nativeApi.appointExpert,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  function appointmentBack(args) {
    let message = {
      data: args,
      plugin: nativeApi.appointmentBack,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  function ask(args) {
    let message = {
      data: args,
      plugin: nativeApi.ask,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  function checkAvailable(args) {
    let message = {
        data: args,
        plugin: nativeApi.checkAvailable,
        sdkVersion: version,
      };

      let callback = function (response) {
        returnValue(response, args);
      };

      _dhbridge.invoke(message, callback);
  }

  // 处理返回结果
  function returnValue(response, args) {
    if (response) {
      if (response.status === 1) {
        if (args.success) {
          args.success(response.data);
        }
      } else if (response.status === 0) {
        if (args.fail) {
          args.fail(response.errorMessage);
        }
      }
    }
    if (args.complete) {
      args.complete(response.complete);
    }
  }

  // 处理返回结果 - primise
  function promisify(data) {
    return new Promise(function (resolve, reject) {
      let callback = function (response) {
        if (response) {
          if (response.status === 1) {
            resolve(response.data);
          } else if (response.status === 0) {
            reject(response.errorMessage);
          }
        } else {
          reject();
        }
      };
      _dhbridge.invoke(data, callback);
    });
  }

  // 分享图片资源 {img: base64StringWithoutType || URL}
  function shareImage(args) {
    let message = {
      data: args,
      plugin: nativeApi.shareImage,
      sdkVersion: version,
    };

    let callback = function (response) {
      returnValue(response, args);
    };

    _dhbridge.invoke(message, callback);
  }

  // native提供的能力
  let nativeApi = {
    onListenEvent: 'common.event',
    ready: 'common.ready',
    showToast: 'common.showToast',
    getImage: 'camera.getImage',
    closeHybird: 'common.closeHybird',
    request: 'net.request',
    getToken: 'user.getToken',
    getMobile: 'user.getMobile',
    getUid: 'user.getUid',
    share: 'knowledge.share',
    login: 'user.login',
    question: 'consultation.question',
    playVideo: 'knowledge.playVideo',
    setColor: 'common.setColor',
    setTitle: 'common.setTitle',
    call: 'common.call',
    appointExpert: 'evaluation.appointExpert',
    appointmentBack: 'consultation.appointmentBack',
    ask: 'consultation.ask',
    shareImage: 'common.shareImage',
    checkAvailable: 'common.checkAvailable',
  };

  window.dhsdk = {
    onListenEvent: onListenEvent,
    ready: ready,
    showToast: showToast,
    closeHybird: closeHybird,
    getImage: getImage,
    request: request,
    getToken: getToken,
    getTokenPromise: getTokenPromise,
    getMobile: getMobile,
    getUid: getUid,
    share: share,
    login: login,
    loginPromise: loginPromise,
    question: question,
    playVideo: playVideo,
    setColor: setColor,
    setTitle: setTitle,
    call: call,
    appointExpert: appointExpert,
    appointmentBack: appointmentBack,
    ask: ask,
    shareImage: shareImage,
    checkAvailable: checkAvailable,
  };
})();
