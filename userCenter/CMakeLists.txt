# 指定cmake编译器的最低版本
cmake_minimum_required(VERSION 3.4.1)

# 要求cmake根据指定的源文件生成库
add_library( # 生成库的名称
             native-lib

             # 设置生成库的类型
             SHARED

             # 所有需要加入到这个库的源文件
             src/main/cpp/native-lib.cpp )

# 如果需要使用系统预构建库，可以使用该方法来查找，比如这里的log库
find_library( # 该变量保存所要关联库的路径
              log-lib

              # 需要关联的库名称
              log )

# 指定需要关联的库
target_link_libraries( # 目标库文件
                       native-lib

                       # 需要在目录库文件中使用的库
                       ${log-lib} )