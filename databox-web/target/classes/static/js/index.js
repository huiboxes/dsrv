{
layui.use(['element', 'jquery', 'layer'], function () {
  let element = layui.element
  let layer = layui.layer
  window.$ = window.jQuery = layui.jquery

  let myUsername = window.localStorage.getItem('username')
  if (myUsername && location.pathname !== '/login' ) {
    $('.layui-side #username').text(myUsername)
    $('.layui-side #avatar img').attr('src','/image/avatar.jpg')
    $('#loggedMenu').css('display','inline')
    $('#notLoggedMenu').css('display','none')
  }

  $('#logout').on('click',function(){
    layer.confirm('确定要退出登录吗?', {icon: 3, title:'提示'}, function(index){
      $.ajax('/logout').then(res=>{
        window.location.href = '/login'
        layer.msg('已退出登录')
        layer.close(index)
      })
    })
  })

})

}