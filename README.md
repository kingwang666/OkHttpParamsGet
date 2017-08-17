# OkHttpParamsGet #
Help you to get Okhttp request params.
<img src="img/get_params.gif"></img>

## How to install ##
File->Settings->Browse Repositories->OkHttpParamsGet

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
<ul>
        <li>
            1.4.0(2017-08-17):
            <ol>
                <li>Auto import class.</li>
            </ol>
        </li>
        <li>
            1.3.0(2017-06-15):
            <ol>
                <li>Update the params name.</li>
                <li>Add get body. This type is return MultipartBody.Builder. You can build a RequestBody and set upload progress listener.</li>
            </ol>
        </li>
        <li>
            1.2.0(2017-03-09):
            <ol>
                <li>Update Id.</li>
                <li>Fix keyboard shortcut cannot use.</li>
                <li>Fix annotations useless bug.</li>
                <li>Fix a AnActionEvents bug. This is because IntelliJ API doesn't allow sharing AnActionEvents between swing events.</li>
            </ol>
        </li>
        <li>
            1.1.0(2017-03-08):
            <ol>
                <li>Optimized the code display</li>
            </ol>
        </li>
        <li>
            1.0(2017-03-08):
            <ol>
                <li>
                    add annotations:
                    <ol>
                        <li>@Ignore: dont add to params</li>
                        <li>@PostFile: post a file</li>
                        <li>@PostFiles: post files</li>
                    </ol>
                </li>
                <li>
                    <a href="https://github.com/kingwang666/OkHttpParamsGet/blob/master/src/com/wang/okhttpparamsget/FileInput.java">FileInput</a>: upload File class
                </li>
            </ol>
        </li>
</ul>


### url ###
[OkHttpParamsGet](https://plugins.jetbrains.com/plugin/9545-okhttpparamsget)