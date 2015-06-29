var tq = {
  doing: false,
  p: {}, // 当前页
  url: "http://i.momia.cn/",
  R: 6378.137
};

//通用工具
tq.t = {
  
  getRad: function(d) {
      return d * Math.PI / 180.0;
    }

  //弹框(比如说用户输入错误的提示框，css样式未写)
  ,
  alert: function(str) {
    var s = '<div class="shide">' + '<div class="alert">' + str + '</div></div>';
    $(document.body).append(s);
  }
  ,
  delshide: function() {
      $('.shide').addClass('none');
      setTimeout(function() {
        $('.shide').remove();
      }, 1000);
    }

  //后退
  ,
  back: function() {
      $('.back').on("click", function() {
        history.back(-1);
      });
  }

  //加载
  ,
  wait: function(msg) {
      var s = '<div class="loading">';
      s += '<div class="ld">';
      s += '<img src="../image/loading.gif" />';
      s += '<p>' + (msg || '加载中...') + '</p>';
      s += '</div></div>';
      $(document.body).append(s);
      tq.doing = true;
    }

  //加载ok
  ,
  waitok: function() {
    $('.loading').remove();
    tq.doing = false;
  }
  ,
  loading: function(done) {
      if (!done) $('.body').append('<p id="p_loading" class="center"><br /><br />加载中...</p>');
      else $('#p_loading').remove();
    }

  //post请求入口
  ,
  post: function(url, para, callback) {
      url = tq.url + url;
      $.post(url, para, function(data) {
        if (data.errno == 0 && !data.status) {
          callback();
        } else {
          alert("erro");
        }
      });
    }

  //get请求入口
  ,
  get: function(url, para, callback) {
      $.get(url, para, function(data) {
        if (data.errno == 0 && !data.status) {
          callback();
        } else {
          alert("erro");
        }
      });
    }

  //加载外部js(方便微信分享，支付等js的引入)
  ,
  loadJs: function(src) {
      var st = document.createElement("script");
      st.src = src;
      document.body.appendChild(st);
    }

  //设置，获取，删除cookie
  ,
  cookie: {
    get: function(key) {
      var arr = new RegExp('\w?' + key + '=(.*?)(;|$)', 'i').exec(document.cookie);
      return arr ? decodeURIComponent(arr[1]) : '';
    },
    set: function(key, val, days) {
      var reg = key + '=' + encodeURIComponent(val);
      if (days) {
        var exp = new Date();
        exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
        reg += "; expires=" + exp.toGMTString();
      }
      reg += '; path=/';
      document.cookie = reg;
    },
    del: function(key) {
      tq.t.cookie.set(key, '', -10);
    }
  }
  
  //获取位置信息
  ,
  getLocation: function(lat2, lng2) {

    navigator.geolocation.getCurrentPosition(function(res) { //获取地理位置成功
      lng1 = res.coords.longitude;
      lat1 = res.coords.latitude;
      loc = (lat1 + ',' + lng1);

      s = tq.t.getDistance(lat1, lng1, lat2, lng2);
      $(".distance").html(s + "km");

    }, function(res) {
      $(".distance").css("display", "none");
    }, {
      enableHighAcuracy: false,
      timeout: 5000,
      maximumAge: 30000
    });
  }

  ,
  getDistance: function(lat1, lng1, lat2, lng2) {

    var radLat1 = tq.t.getRad(lat1);
    var radLat2 = tq.t.getRad(lat2);

    var a = radLat1 - radLat2;
    var b = tq.t.getRad(lng1) - tq.t.getRad(lng2);
    var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
    s = s * tq.R;
    s = Math.round(s * 10000) / 10000.0;
    s = parseInt(s);
    return s;
  }
  ,
  getScrollImg: function() {
    var slider = Swipe(document.getElementById('scroll_img'), {
      auto: 3000,
      continuous: true,
      callback: function(pos) {
        var i = bullets.length;
        while (i--) {
          bullets[i].className = ' ';
        }
        bullets[pos].className = 'on';
      }
    });
    var bullets = document.getElementById('scroll_position').getElementsByTagName('li');
  }
}

//各个页面的js

tq.home = {

  //登录
  login: function(odiv, phonetext, codetext, submit) {
    var InterValObj; //timer变量，控制时间
    var count = 30; //间隔函数，1秒执行
    var curCount; //当前剩余秒数
    odiv.on('click', function() {
      sendMessage();
    });

    submit.on("click",function(){
      var phone = phonetext.val(); //手机号码
      var code = codetext.val();
      tq.t.post("auth/login", {
        mobile: phone,code: code
      }, login_suc);
    })

    function login_suc(){
      alert(1);
    }

    function sendMessage() {
      curCount = count;
      var phone = phonetext.val(); //手机号码
      tq.t.post("auth/login", {
        mobile: phone
      }, suceess);
    }

    function SetRemainTime() {
      if (curCount == 1) {
        window.clearInterval(InterValObj); //停止计时器
        odiv.removeAttr("disabled"); //启用按钮
        odiv.removeClass("colddown");
        odiv.html("重新发送");
      } 
      else {
        odiv.html(curCount + "秒后重试");
        tq.t.waitok();
        curCount--;
      }
    }

    function suceess() {
      odiv.attr("disabled", "true");
      tq.t.wait();
      odiv.val(curCount + "秒后重试");
      odiv.addClass("colddown");
      InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
    }
  }

  //获取活动列表数据
  ,
  getActsList: function(wrap) {
    api = tq.url + "home?pageindex=0&city=1";
    // alert(api);
    $.get(api, {}, function(data) {
      // alert(api);
      var data = data.data.products;
      for (var i = 0; i < data.length; i++) {
        var id = data[i].id;
        var s = '<div class="act_list">';
        s += "<a href=actsDetail.html?id=" + id + ">";
        s += '<img src=' + data[i].cover + ' />'; //获取图片
        s += '<div class="act_detail">';
        s += '<h3>' + data[i].title + '</h3>' ;//获取title
        s += '<p class="address">' + data[i].address + '&nbsp·<i class="distance"></i></p>' ;//获取地址
        s += '<div class="act_attend">';
        s += '<span class="act_time">' + data[i].scheduler + '</span>'; //获取活动时间
        s += '<span class="act_num"><i>' + data[i].joined + '</i>人报名</span>'; //获取报名人数
        s += '<span class="act_price">￥<i>' + data[i].price + '</i></span>' ;//获取报名费用
        s += "</div></div></a></div>";
        $(wrap).append(s);
        var poi = data[i].poi;
        var lat2 = parseFloat(poi.split(":")[1]);
        var lng2 = parseFloat(poi.split(":")[0]);

        tq.t.getLocation(lat2, lng2);
      }
    });
  }

  //获取首页轮播图片数据
  ,
  getIndexScrollImg: function(wrap) {
    api = tq.url + "home?pageindex=0&city=1";
    $.ajax({
      type: "get",
      url: api,
      async: false,
      success: function(data) {
        var data = data.data.banners;
        if (data.length == 0) {
          $(wrap).css("display", "none");
        } else if (data.length == 1) {
          for (var i = 0; i < data.length; i++) {
            var s = "<a href='" + data[i].action + "' style='display:block'><img src='" + data[i].cover + "' width='100%' /></a>";
          }
          $(wrap).append(s);
        } else {
          var ul1 = "<ul class = 'scroll_wrap'>";
          for (var i = 0; i < data.length; i++) {
            ul1 += "<li><a href='" + data[i].action + "'><img src='" + data[i].cover + "' width='100%' /></a></li>";
          }
          ul1 += "</ul>";

          var ul2 = "<ul class='scroll_position' id='scroll_position'>";
          for (var j = 0; j < data.data.imgs.length; j++) {
            if (j == 0) {
              ul2 += "<li class='on'><a href='javascript:void(0);'</a></li>";
            } else {
              ul2 += "<li><a href='javascript:void(0);'</a></li>";
            }
          }
          ul2 += "</ul>";
          $(wrap).append(ul1).append(ul2);
          wrap_li = $(".scroll_position");
          tq.t.getScrollImg();
        }
      },

    });
  }

  //详情页数据获取;
  ,
  getDetailScrollImg: function(wrap) {
    var url = window.location.href;
    var param = url.split("?");
    var id = param[1].substring(3, param[1].length);
    api = tq.url + "product?id=" + id;
    $.ajax({
      type: "get",
      url: api,
      async: false,
      success: function(data) {

        //获取顶部轮播
        var data1 = data.data.imgs;
        if (data1.length == 0) {
          $(wrap).css("display", "none");
        } else if (data1.length == 1) {
          alert(1);
        } else {
          var ul1 = "<ul class = 'scroll_wrap' id='scroll_wrap'>";
          for (var i = 0; i < data1.length; i++) {
            ul1 += "<li><img src='" + data1[i] + "' width='100%' /></li>";
          }
          ul1 += "</ul>";

          var ul2 = "<ul class='scroll_position' id='scroll_position'>";
          for (var j = 0; j < data1.length; j++) {
            if (j == 0) {
              ul2 += "<li class='on'><a href='javascript:void(0);'</a></li>";
            } else {
              ul2 += "<li><a href='javascript:void(0);'</a></li>";
            }
          }
          ul2 += "</ul>";
          $(wrap).append(ul1).append(ul2);
          wrap_li = $("#scroll_position");
          tq.t.getScrollImg();
        }

        //获取活动流程，活动特色等数据
        var data2 = data.data.content;
        for (var i = 0; i < data2.length; i++) {
          if (data2[i].style === "ol") {
            var r = '<div class="tips_list">';
            r += '<h3>' + data2[i].title + '</h3>';
            r += '<ol class="tips_article spec1">';
            for (var j = 0; j < data2[i].body.length; j++) {
              if (data2[i].body[j].link == "" || data2[i].body[j].link == undefined) {
                r += '<li>' + data2[i].body[j].text + '</li>';
              }
            }
            r += '</ol>';
            for (var j = 0; j < data2[i].body.length; j++) {
              if (data2[i].body[j].link != "" && data2[i].body[j].link != undefined) {
                r += '<div class="word_img">';
                r += '<a href = ' + data2[i].body[j].link + '>' + data2[i].body[j].text + '<span class="more01"></span></a>';
                r += '</div>';
              }
            }
            r += '</div>';
            $(".content_list").append(r);
          } else if (data2[i].style === "ul") {
            var r = '<div class="tips_list">';
            r += '<h3>' + data2[i].title + '</h3>';
            r += '<ul class="tips_article">';
            for (var j = 0; j < data2[i].body.length; j++) {
              r += '<li>' + data2[i].body[j].text + '</li>';
            }
            r += '</ul></div>';
            $(".content_list").append(r);
          } else {
            var r = '<div class="tips_list">';
            r += '<h3>' + data2[i].title + '</h3>';
            r += '<p class="tips_article spec">';
            for (var j = 0; j < data2[i].body.length; j++) {

              if (data2[i].body[j].label != "" && data2[i].body[j].label != undefined) {
                r += '<span class="orange">' + data2[i].body[j].label + ':</span>' + data2[i].body[j].text + '<br>';
              } else if (data2[i].body[j].img != "" && data2[i].body[j].img != undefined) {
                r += '<img src= ' + data2[i].body[j].img + '><br>';
              } else {
                r += data2[i].body[j].text;
              }

            }
            r += '</ul></div>';
            $(".content_list").append(r);
          }
        }

        //获取title等信息
        var data3 = data.data.customers;
        var m = '<h3>' + data3.text + '</h3>';
        m += "<span class='more'></span>";
        m += "<div style='clear:both'></div>";
        for (var i = 0; i < data3.avatars.length; i++) {
          m += "<img src = " + data3.avatars[i] + " />";
        }
        m += "<div style='clear:both'></div>";
        $(".attent_total").append(m);

        var n = "<h3>" + data.data.title + "</h3>";
        n += "<div class='act_attend'>";
        n += "<span class='num'><i>" + data.data.joined + "</i>人报名</span>";
        n += "<span class='act_price orange'>￥<i>" + data.data.price + "</i></span>";
        n += "</div>";
        $(".act_detail").append(n);

        var k = "<p class='child_age'><img src='image/age.png'>" + data.data.crowd + "</p>";
        k += "<p class='tel'><img src='image/time.png'>" + data.data.scheduler + "</p>";
        k += "<p class='address'><img src='image/address.png'>" + data.data.address + "</p>";
        $(".tips").append(k);

        tq.t.back();
      }
    });
  }

  //
  ,getSKU: function(){
    api = tq.url + "product/order?id=37&utoken=123"; 
    $.get(api, {}, function(data){
      var data = data.data;
      var arr_stock = [];
      var arr_num = [];
      for(var i=0; i<data.skus.length; i++){
        arr_stock.push(data.skus[i].stock);
        if(i == data.skus.length - 1){
          var s = "<div class='form01' id='last'>";
        }else{
          var s = "<div class='form01'>";
        }
        s += "<div class='pad_bot left'>";
        s += "<p class='time'>"+data.skus[i].time+"</p>";
        s += "<span class='price'><i class='orange'>￥"+data.skus[i].minPrice+"</i>起</span>";
        s += "<span class='num'>仅剩"+data.skus[i].stock+"个名额</span>";
        s += "</div>";
        if(i == 0){
          s += "<div class='chk right'></div>";
        }
        else{
          s += "<div class='chk right none'></div>";
        }
        s += "<div style='clear:both'></div>";
        s += "</div>";
        $("#chk_time").append(s);

        var data1 = data.skus[i].prices;
        if(i == 0){
          var m = "<div class='order_detail fee' id='chk_fee'>"
        }else{
          m = "<div class='order_detail fee none' id='chk_fee'>"
        }
        for(var j=0; j<data1.length; j++){
          if(j == data1.length - 1){
            m += "<div class='form01 fee' id='last'>";
          }else{
            m += "<div class='form01 fee'>"
          }
          m += "<div class='pad_bot left'>";
          if(data1[j].adult != 0 && data1[j].child != 0){
            m += "<p class='time'>"+data1[j].adult+"成人"+data1[j].child+"儿童：￥<i class='orange fee1'>"+data1[j].price+"</i>/"+data1[j].unit+"</p>";
          }
          else if( data1[j].adult == 0 ){
            m += "<p class='time'>"+data1[j].child+"儿童：￥<i class='orange fee1'>"+data1[j].price+"</i>/"+data1[j].unit+"</p>";
          }else if(data1[j].child == 0){
            m += "<p class='time'>"+data1[j].adult+"成人：￥<i class='orange fee1'>"+data1[j].price+"</i>/"+data1[j].unit+"</p>";
          }
          m += "</div>";
          m += "<div class='chk_num right'>";
          m += "<span class='minus gray'>-</span>";
          m += "<span class='num01'>0</span>";
          m += "<span class='plus green'>+</span>";
          m += "</div>";
          m += "<div style='clear:both'></div>";
          m += "</div>";
        }
        m += "</div>"
        $("section .chk_fee").append(m);
      }
      if(data.skus.length > 3){
        var k = "<div class='form02 last'>";
        k += "<p class='chk_time r'>选择其他场次</p>";
        k += "</div>";
        $("#chk_time").append(k);
      }

      var form01 = $(".time .form01");
      var fee = $(".chk_fee .order_detail");

      for(var i=3; i<form01.length; i++){
        $(form01[i]).addClass("none");
      }

      $(".form02 .chk_time").on("click",function(){
        $(form01).removeClass("none");
        $(".form02 .chk_time").css("display","none");
      }); 

      $(form01).on("click",function(){
        var index = $(form01).index(this);
        $(fee).addClass("none");
        $(".form01 .chk").addClass("none");
        $(fee[index]).removeClass("none");
        $(this).find(".chk").removeClass("none");
      });

      //计算总价
      function totalMoney(){
        var total_price = 0;
        var fee_form = $(".order_detail .fee");
        for(var i=0; i<fee_form.length; i++){
          var price = parseFloat($(fee_form[i]).find(".fee1").html());
          var num = parseInt($(fee_form[i]).find(".num01").html());
          total_price += price * num;
        }
        $(".chk_sub .total_price i").html("￥"+total_price.toFixed(2));
      }

      // 减号样式
      function count(){
          $(".num01").each(function(){
              var num = parseInt($(this).html());
              arr_num.push(num);
              if (num == 0) {
                  $(this).siblings(".minus").removeClass("green").addClass("gray");
                  $(this).siblings(".minus").attr("disabled","disabled");
              } else {
                  $(this).siblings(".minus").removeClass("gray").addClass("green");
              }
          });
      }

      $(".order_detail .plus").on("click",function(){
        var input = $(this).siblings(".num01");
        // console.log($(this).parent(".order_detail").length);
        input.html(parseInt(input.html()) + 1);
        count();
        totalMoney();
      });

      $(".minus").on("click",function(){
        var input = $(this).siblings(".num01");
        input.html(parseInt(input.html()) - 1);
        count();
        totalMoney();
      });

      console.log(arr_stock);
      //获取联系人信息
      $(".show_detail .chk_info .chk_phone i").html(data.contacts.mobile.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2'));
    });
  }
}