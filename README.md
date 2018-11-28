# Geological

## App功能

#### Android平台App的开发
 - 基于Material Design实现了App的基本UI。
 - 基于Android平台相关组件完成了图片拍摄，保存，上传和下载显示基本功能。
 - 基于Android平台相关组件完成了用户管理，图片管理和地质预测客户端的附加功能。
 - 使用网络和GPS保存图片的地理信息，并上传至服务器功能完成。

#### 后台服务器的开发
 - 使用Python框架Flask实现服务器端开发。
 - 基于SQLAlchemy建立后台数据库保存用户信息，用户上传图片和用户操作记录。
 - 开发算法，实现图片中岩芯的提取，图片裁剪和拼接。
 - 算法模块嵌入服务器的事务逻辑，将从客户端App收到的图片进行处理、记录并返回。

#### 算法模块研发
 - 岩芯图片的预处理、提取，裁剪，拼接均已实现。
 - 根据相应指标预测地质状况的模块已完成。
 - 岩石种类的识别由于训练需要的有标记数据缺失，仍然有待进一步的探索。

## Demo

#### 注册和登录 主界面

<figure class="half">
    <img src="https://img1.doubanio.com/view/status/m/public/7ac443a0b86d1d7.webp"  width="300px"/><img src="https://img3.doubanio.com/view/status/m/public/1fc4084e41edbbf.webp"  width="300px"/>
</figure>

#### 图像切割与拼接

<figure class="half">
    <img src="https://img1.doubanio.com/view/status/m/public/8fd331628e5ad8c.webp"  width="300px"/><img src="https://img1.doubanio.com/view/status/m/public/8a528ff694500bb.webp"  width="300px"/>
</figure>

#### 地质预测

<center class="half">
    <img src="https://img3.doubanio.com/view/status/m/public/42612d4a49ddcbf.webp" width="300px"/><img src="https://img3.doubanio.com/view/status/m/public/9b88c3d557f4322.webp" width="300px"/>
</center>
