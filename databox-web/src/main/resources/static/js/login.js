{
layui.use(['form','jquery'], function(){
  let form = layui.form
  let layer = layui.layer
  window.$ = window.jQuery = layui.jquery

  form.on('submit(login)', async function(data){
    const { username,password } = data.field
    const correctUser = /^[a-zA-Z0-9_-]{6,16}$/.test(username)
    const emptyPwd = password.length === 0
    if (!correctUser || emptyPwd) {
      layer.msg('账号或者密码错误')
      return false
    }
    const res = await $.ajax({
      method: 'GET',
      url: '/loginPost',
      data: {
        username,
        password,
      },
    })

    if (res.code === 200) {
      localStorage.setItem("username",username)
      window.location.href = 'dashboard'
    }else{
      layer.msg('登录出错，请重试')
    }
    return false
  })
})
}