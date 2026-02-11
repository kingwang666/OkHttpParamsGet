# OkHttpParamsGet #
帮助你自动生成Okhttp请求参数，支持kotlin。  
**这是IntelliJ IDEA and Android Studio的插件版本。 如果你需要[gradle插件版本](https://github.com/kingwang666/HttpParam) 请移步.**  
<img src="img/getbody.gif" width="600"></img> <img src="img/file.jpg" width="300"></img>

## How to install ##
File->Settings->Browse Repositories->OkHttpParamsGet

## Support ##
      
1. Map&lt;String, String&gt;  
2. Map&lt;String, Object&gt;
3. Map&lt;String, RequestBody&gt;
4. List&lt;MultipartBody.Part&gt;
5. MultipartBody.Builder
## How to use ##
1. 选择一个文件
2. 快捷键 Alt + P 或者 右键Generate点击GetParams
3. 选择一个生个参数类型
4. 点击ok
## ps ##
- @Ignore: 不添加进自动生成的请求参数中
- @PostFile: 文件参数
- @ParamName: 自定义请求参数名称
- <span style="color:#59ABFD"><S>@PostFiles: 多个文件参数</S> 已经移除请用@PostFile替代</span>
- @Key: 上传文件的Key值
- @Filename: 上传文件的文件名
- @MimeType: 上传文件的mime-type
- @Data: 上传的文件  
这些注解可以放在任何包下


### url ###
[OkHttpParamsGet](https://plugins.jetbrains.com/plugin/9545-okhttpparamsget)