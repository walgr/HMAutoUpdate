    鸿蒙自动更新工具

    部署后请求接口：
    1.uploadHap PUT
    参数:package、appVersion、file、savaFileName（可选）
    功能：保存上次的hap包并安装到手机上，按照程序运行目录下的clientInfo.txt配置文件连接手机
    2.retryInstallNewHap POST   
    参数:package、appVersion
    功能：重新安装最新的hap到手机上