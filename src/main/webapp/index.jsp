<html>
<head>
    <meta charset="utf-8"/>
</head>
<body>
<h2>Hello World!</h2>

<!--普通文件上传-->
<form name="uploadFileForm" action="/manage/product/upload.do" enctype="multipart/form-data" method="post">
    <input type="file" name="upload_file" />
    <input type="submit" value="上传文件测试"/>
</form>


<!--富文本文件上传-->
<form name="uploadFileForm" action="/manage/product/richtext_img_upload.do"
        enctype="multipart/form-data" method="post">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片文件上传" />
</form>


</body>
</html>
