### 1. 项目介绍
> 该项目是基于Vue3 + SpringBoot + Redis的移动端app，用户可以通过该平台来创建属于自己的学习小组队伍同时也可以搜索寻找到自己感兴趣的队伍加入然后一起来交流学习
### 2. git的操作：
  - 删除文件步骤：
    1. 首先需要将远程仓库的代码拉取到本地  git pull origin master
    2. 然后可以使用dir命令查看需要删除的文件或者文件夹  dir
    3. 接着使用命令删除文件或文件夹 git rm -r --cached "文件或者文件夹名"
    4. 然后提交到本地的仓库 git commit -m "..."
    5. 最后推送到远程仓库即可 git push origin master
  - git提交代码到远程仓库的步骤
    1. 首先在项目的文件夹位置创建一个本地的git仓库  git init
    2. 然后使用git add 方法选择需要提交的文件夹  git add 文件夹
    3. 接着使用commit命令提交到本地仓库中  git commit -m '注释'
    4. 然后使用命令链接远程仓库，一定是ssh的链接地址  git remote add origin + 远程仓库的ssh地址
    5. 最后使用命令将本地仓库里面的代码推送到远程仓库 git push -u origin [分支名]
  - git 修改文件（上传重新修改的项目代码或者新的文件如何操作）
    1. 首先需要将远程仓库的代码拉取到本地  git pull origin master   [一定要先拉取到本地之后再进行更改，否则就会报错]
    2. 然后再使用git add将修改的文件或者文件夹进行追踪  git add 文件或文件名
    3. 接着将代码提交到本地仓库 git commit -m '注释'
    4. 最后推送到远程仓库即可  git push origin master

