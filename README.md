# OkHttpParamsGet #
Help you to get Okhttp request params.
<img src="img/get_params.gif"></img>

## Support ##
      
1. Map&lt;String, String&gt;  
2. Map&lt;String, RequestBody&gt;
3. List&lt;MultipartBody.Part&gt;
4. MultipartBody.Builder
## How to use ##
1. Choose a class.
2. Press Alt + P or click the Generate menu choose GetParams.
3. Choose a type.
4. click ok.
## ps ##
- @Ignore: dont add to params
- @PostFile: post a file 
- @PostFiles: post files  
These annotations can be in any package

## update ##
- 1.3.0(2017-06-15):
  1. Update the params name.
  2. Add get body. This type is return MultipartBody.Builder. You can build a RequestBody and set upload progress listener.
- 1.2.0(2017-03-09):
  1. Update Id.
  2. Fix keyboard shortcut cannot use.
  3. Fix annotations useless bug.
  4. Fix a AnActionEvents bug. This is because IntelliJ API doesn't allow sharing AnActionEvents between swing events.
- 1.1.0(2017-03-08):
  1. Optimized the code display
- 1.0(2017-03-08):
  1. init. add annotations  
  @Ignore: dont add to params   
  @PostFile: post a file   
  @PostFiles: post files  


### url ###
[OkHttpParamsGet](https://plugins.jetbrains.com/plugin/9545-okhttpparamsget)