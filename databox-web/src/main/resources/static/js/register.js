{
  layui.use(['form','jquery'], function(){
    let form = layui.form
    let layer = layui.layer
    window.$ = window.jQuery = layui.jquery

    form.on('submit(register)', async function(data){
      const { username,password } = data.field
      const correctUser = /^[a-zA-Z0-9_-]{6,16}$/.test(username)
      const errPwd = password.length < 6
      if (!correctUser) {
        layer.msg('账号格式错误')
        return false
      }else if(errPwd) {
        layer.msg('密码格式错误')
        return false
      }
      const res = await $.ajax({
        method: 'POST',
        url: '/dx/sys/register',
        data: {
          username,
          password,
        },
      })
      if (res.code === 200) layer.msg('注册成功')
      return false
    })
  })
}
