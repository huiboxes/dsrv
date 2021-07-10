{
    layui.use(['jquery'], function(){
      let layer = layui.layer
      window.$ = window.jQuery = layui.jquery
      window.filepath = '/'

      $.ajax('dx/bucket/list').then(res=>{
        $('#file-list').html(filebarGenerator(res))
      })

      $('#file-list').on('dblclick','.file-bar',function(){
        let currentName = $(this)[0].innerText
        if(window.filepath === '/'){
          getFilelist(currentName,'/').then(res=>{
            window.filepath = `/${currentName}/`
            $('#file-list').html(filebarGenerator(res.objectList))
            $('#fileNavBreadcurmb').html(`<a href="/dashboard"><i class="layui-icon layui-icon-home"></i></a><span lay-separator="">/</span>
                                          <a href="/dashboard">全部文件</a><span lay-separator="">/</span><a href="javascript:;">${currentName}</a>`)
          })
        }else{
          let bucket = filepath.split('/')[1]
          let newFilepath = window.filepath + `${currentName}/`
          let dir = newFilepath.replace(`/${bucket}`,'')
          getFilelist(bucket,dir).then(res=>{
            window.filepath = newFilepath
            $('#file-list').html(filebarGenerator(res.objectList))
            $('#fileNavBreadcurmb').html(breadcurmbGenerator(window.filepath))
          })
        }

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

        let info = `<div class="right">
                      <span class="info">` +
                        (isFile ? `<i class="layui-icon layui-icon-template-1"></i>: ${file.length}
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

}