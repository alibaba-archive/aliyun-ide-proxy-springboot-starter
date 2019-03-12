# 功能描述

这是一个spring boot starter的demo

# 需要修改的地方

- 配置类

    demo中的配置类名为MyProperties(不修改也可以用),如果您有代码洁癖，
    
    请修改该类名为您所期望的。
    
- Configuration类的开关开启的条件

  详见注释
  
# 发布
- 本地发布测试

  插件开发完成后可以使用发布命令:
    ```
    mvn clean install
    ```  
  这样插件发布到本地的maven依赖仓库中，可直接在其他工程引用。    
