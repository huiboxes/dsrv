layui.use(['element', 'jquery', 'layer'], function () {
  let element = layui.element
  let layer = layui.layer
  window.$ = window.jQuery = layui.jquery

//  let myInfo = JSON.parse(window.localStorage.getItem('myInfo'))
//  if (myInfo) {
//    $('#myName').text(myInfo.username)
//  }
//
//  const fetchInfo = {
//    url: fetchMyInfo,
//    success: function (res) {
//      window.localStorage.setItem('myInfo', JSON.stringify(...res.data))
//    },
//  }
//
//  if (myInfo === null) {
//    try {
//      $.ajax(fetchInfo).then(res => {
//        $('#myName').text([...res.data][0].username)
//      })
//    } catch (error) {
//      layer.msg('网络异常，请重试')
//      console.log(error)
//    }
//  }
//
//  // 点击侧边栏切换主展示区内容
//  $('.settingItem').click(function (e) {
//    e.preventDefault()
//    $('#booth').attr('src', $(this).attr('href'))
//  })
//
//  // 点击LOGO 返回主展示区
//  $('.layui-logo').on('click', function () {
//    $('#booth').attr('src', './dashboard/baseBooth.html')
//  })
//
//
//  $('#logout').on('click', function () {
//    const index = layer.load(2, { shade: [0.3, '#393D49'] })
//    setTimeout(() => {
//      layer.close(index)
//    }, 400)
//
//    window.localStorage.removeItem('Authorization')
//    window.localStorage.removeItem('myInfo')
//    window.localStorage.removeItem('role')
//    window.localStorage.removeItem('redirect')
//    myInfo = null
//    window.location = 'index.html'
//  })
})
