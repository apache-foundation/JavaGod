## 如何贡献知识

### 创建一个新的分类
因为每个分类可能运行的 Java 环境不同，所以我们使用Module将他
分割开来。
首先我们在JavaGod根目录创建一个Module
![](resource/img/newm.png)
然后指定groupId 以及 artifact id
![](resource/img/groupid.PNG)
就可以创建一个新的分类了

### 为分类添加文档
你可以采取 JavaDoc 注释的方式在类文件内添加注释
也可以在你需要添加注释的模块的根目录下创建README.md 文件

### 合并代码 
我们采取的方式是使用pr的方式
因为组里的大家都有写的限权，对于这个项目
首先通过   
`git clone https://github.com/apache-foundation/JavaGod`    
的方式下载代码
然后对切出一个你自己的分支
`git checkout -b yours`    
更改后使用 commit 提交    
再采用`git push origin yours:yours` 推送到远程仓库
再在 github 页面申请pull request 

对于别人的特性
我们采取     
`git checkout master`
`git pull origin master`
拉取 master 最新的代码 
然后切换到你自己的分支  
`git checkout yours`
使用 `git rebase master`   
将你自己的分支变基为 master 就可以达到更新最新代码的效果


### 注意
你需要保证你的代码一定要跑起来，因为只有能跑起来的代码才能
方便大家打断点调试。