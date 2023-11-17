# 米瑞斯汽车门店管理平台

> 基于若依框架开发的前后端分离汽车门店管理平台（CarO2O）

## 项目环境

- SpringBoot 2.5.14
- Vue 3.7.0
- MySQL 5.7
- Redis 6.9
- JDK 8
- NodeJS 12.22.2

## 后端

### MySQL配置
> 进入`ms-admin\src\main\resources\application-druid.yml`修改MySQL数据库地址、用户名、密码

### Redis配置
> 进入`ms-admin\src\main\resources\application.yml`修改Redis地址、密码（无密码可不填写）

### 启动主类
> `CarO2OApplication.java`

![](https://cdn.jsdelivr.net/gh/starmanMS/blog-images@main/img/20231117231740.png)

## 前端
**前端项目文件夹ms-ui**

### 依赖安装
>npm install --registry=https://registry.npmmirror.com

### 启动
> npm run dev

![](https://cdn.jsdelivr.net/gh/starmanMS/blog-images@main/img/20231117232037.png)

### 登录界面
`admin/YUNjing`
![](https://cdn.jsdelivr.net/gh/starmanMS/blog-images@main/img/20231117232000.png)