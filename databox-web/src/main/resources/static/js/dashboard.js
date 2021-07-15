{
    layui.use(['jquery','upload'], function(){
      let layer = layui.layer
      let upload = layui.upload;
      window.$ = window.jQuery = layui.jquery
      window.filepath = '/'

      // load file list
      onInitGetFilelist()

      // refresh page
      $('#refresh').on('click',function(){
        window.location.reload()
      })

      // refresh file list
      $('#file-list').on('dblclick','.file-bar',function(){
        if(!$(this).find('.layui-icon-download-circle').length){
          let currentName = $(this)[0].innerText
          if(window.filepath === '/'){
            getFilelist(currentName,'/').then(res=>{
              window.filepath = `/${currentName}/`
              res.objectList.length === 0
                        ? $('#file-list').html('<h2 style="user-select:none;">当前位置没有文件</h2>')
                        : $('#file-list').html(filebarGenerator(res.objectList))
              $('#fileNavBreadcurmb').html(`<a href="/dashboard"><i class="layui-icon layui-icon-home"></i></a><span lay-separator="">/</span>
                                            <a href="/dashboard">全部文件</a><span lay-separator="">/</span><a href="javascript:;">${currentName}</a>`)
            })
          }else{
            let bucket = filepath.split('/')[1]
            let newFilepath = window.filepath + `${currentName}/`
            let dir = newFilepath.replace(`/${bucket}`,'')
            getFilelist(bucket,dir).then(res=>{
              window.filepath = newFilepath
              res.objectList.length === 0
                        ? $('#file-list').html('<h2 style="user-select:none;">当前位置没有文件</h2>')
                        : $('#file-list').html(filebarGenerator(res.objectList))
              $('#fileNavBreadcurmb').html(breadcurmbGenerator(window.filepath))
            })
          }
        }
      })

      // download
      $('#file-list').on('click','.layui-icon-download-circle',function(){
        let bucket = window.filepath.split('/')[1]
        let filename = $(this).parent().parent().parent().find('.key').text()
        let key = window.filepath.replace(`/${bucket}`,'') + filename
        let url = `/dx/object/content?bucket=${bucket}&key=${key}`

        let a = document.createElement('a')
        a.href = url
        a.crossOrigin = 'Anonymous'
        a.download = filename
        // a.target = '_blank'
        a.click()
      })

      // delete
      $('#file-list').on('click','.layui-icon-delete',function(){
        let currentName = $(this)[0].innerText
        let bucket = window.filepath.split('/')[1]
        let filename = $(this).parent().parent().parent().find('.key').text()
        let isFile = $(this).parent().children().length > 1
        let key = window.filepath.replace(`/${bucket}`,'') + filename
        let dir = window.filepath.replace(`/${bucket}`,'')

        layer.confirm('确定要删除该文件吗？', function (index) {
          let url
          if ( isFile ){
            url = `/dx/deleteFile?bucket=${bucket}&key=${key}`
          }else if(window.path === '/') {
           layer.msg('暂时不支持删除根目录')
           return false
//          console.log(1)
//            bucket = $(this).parent().parent().parent().find('.key').text()
//            url=   `/dx/deleteBucket?bucket=/${bucket}`
          }else {
          console.log(2)
            url = `/dx/deleteDir?bucket=${bucket}&key=${key}/`
          }
          var settings = {
            "url": url,
            "method": "POST",
            error: function(err){
              layer.msg('当前目录不为空')
            }
          }

          $.ajax(settings).done(function () {
            if( !isFile && window.filepath === '/' ){
              onInitGetFilelist()
            }else{
              refreshCurrentPage(bucket,dir)
            }

            layer.msg('删除成功')
            layer.close(index)
          })
        })
      })

      // upload
      $('#uploadBtn').on('click',function(){
        $('#upload')[0].click()
      })

      $('#upload').on('change',handleFile)

      // new folder
      $('#newFolder').on('click',function(){
        if(window.filepath === '/') {
          layer.msg('暂时不支持创建根目录')
          return false
        }

        layer.prompt({
          title: '创建文件夹（支持递归创建如：/dir1/dir2/）'
        }, function(value, index, elem){
          if (value.length < 1) {
            layer.msg('请输入正确的文件夹名称')
          }
          let bucket = window.filepath.split('/')[1]
          let dir = window.filepath.replace(`/${bucket}`,'')

          if (!value.startsWith('/')){
            value = '/' + value
          }else if(value.endsWith('/')){
            value = value + '/'
          }
          let key = dir + value.substr(1,value.length-1)

          var settings = {
            "url": `/dx/object?bucket=${bucket}&key=${key}`,
            "method": "POST"
          };

          $.ajax(settings).done(function (response) {
            refreshCurrentPage(bucket,dir)
          })

          layer.close(index)
        })
      })

      // search
      $('#file-search-btn').on('click',function(){
        if(window.filepath === '/'){
          layer.msg('文件被整理到不需要用搜索功能了')
        }
        let keywords = $('#search-input').val()
        if (keywords.length === 0){
          layer.msg('搜索关键词不能为空')
        }

        let bucket = window.filepath.split('/')[1]
        let dir = window.filepath.replace(`/${bucket}`,'')

        var settings = {
          url: `/dx/object/list/prefix?bucket=${bucket}&dir=/&prefix=${keywords}`,
          method: "GET",
        };

        $.ajax(settings).done(function (res) {
          res.objectList.length === 0
                    ? $('#file-list').html(`<h2 style="user-select:none;">没能根据“${keywords}”搜索到结果</h2>`)
                    : $('#file-list').html(filebarGenerator(res.objectList))
        })
      })

    })

    /***********  tool function  ************************************************/
    function filebarGenerator(fileList){
      fileList.sort((a,b)=>a.length - (b.length || 0))
      let filebarList = ''
      for (file of fileList){
        let isFile = !!file.length
        let icon = `<div class="file-bar">
                        <div class="left">
                          <span class="icon">
                          <svg class="icon" aria-hidden="true">
                            <use xlink:href="#` +
                              (isFile ? 'icon-wenjian' : 'icon-wenjianjia1') +
                              `"></use></svg></span>`

        let name = `<span class="key">${file.bucketName || file.name }</span></div>`
        file.length && (file.length = `${file.length}`.slice(0,`${file.length}`.length-3) + '.' + `${file.length}`.slice(`${file.length}`.length-3,`${file.length}`.length))
        let info = `<div class="right">
                      <span class="info">` +
                        (isFile ? `<i class="layui-icon layui-icon-template-1"></i>: ${file.length} KB
                                   <i class="layui-icon layui-icon-download-circle"></i>` : '')
                        + `<i class="layui-icon layui-icon-delete"></i>
                      </span>
                    </div>
                  </div>`

        let filebar = icon + name + info
        filebarList += filebar
      }
      return filebarList
    }

    function breadcurmbGenerator(filepath){
        filepath = filepath.split('/')
        filepath.pop()
        filepath.shift()
        let breadItem = ''
        filepath.forEach((item,index)=>{
            if( index === filepath.length-1 ){
                breadItem += `<a href="javascript:;"><cite>${item}</cite>`
            }else{
                breadItem += `<a href="javascript:;">${item}</a></a><span lay-separator="">/</span>`
            }
        })
        return `<a href="/dashboard"><i class="layui-icon layui-icon-home"></i></a><span lay-separator="">/</span>
                <a href="/dashboard">全部文件</a></a><span lay-separator="">/</span>` + breadItem
    }

    function getFilelist(bucket,dir){
        return $.ajax({
                 url: 'dx/object/list/dir',
                 data: {
                   bucket,
                   dir
                 }
               })
    }

    function refreshCurrentPage(bucket,dir){
      getFilelist(bucket,dir).then(res=>{
         res.objectList.length === 0
                 ? $('#file-list').html('<h2 style="user-select:none;">当前位置没有文件</h2>')
                 : $('#file-list').html(filebarGenerator(res.objectList))
       })
    }

    function onInitGetFilelist(){
      $.ajax('dx/bucket/list').then(res=>{
          res.length === 0
            ? $('#file-list').html('<h2 style="user-select:none;">当前位置没有文件</h2>')
            : $('#file-list').html(filebarGenerator(res))
        })
    }

    function handleFile(){
      if(window.filepath === '/') {
        layer.alert('根目录不可直接上传文件', {
          icon: 2,
          title: "提示"
        })
        return false
      }

      let formData = new FormData()
      let fs = $("#upload")[0].files;
      let max_size = 1024 * 1024 * 500
      let bucket = window.filepath.split('/')[1]
      let dir = window.filepath.replace(`/${bucket}`,'')
      let key = ''
      let mediaType = ''
      let url = '/dx/object'

      for (let i = 0; i < fs.length; i++) {
        let d = fs[0]
        if(d.size <= max_size){
          formData.append("content", fs[i])
          mediaType = fs[i].type
          key = dir + `${fs[i].name}`
        }else{
          layer.alert('上传文件大于500MB，请使用客户端', {
            icon: 2,
            title: "提示"
          });
          return false
        }
      }

      $.ajax({
        type: "POST",
        url: url + `?bucket=${bucket}&key=${key}&mediaType=${mediaType}`,
        data: formData,
        cache: false,
        processData: false,
        contentType: false
      }).then(res=>{
         layer.msg('上传成功')
         refreshCurrentPage(bucket,dir)
      })
    }

}