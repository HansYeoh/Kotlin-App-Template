# Template

一个基于 [KernelSU Manager](https://github.com/tiann/KernelSU) UI 框架构建的 Android 应用模板项目。

## 项目来源

本项目源自 [KernelSU](https://github.com/tiann/KernelSU) 的 Manager 应用模块。KernelSU 是一个运行在 Android GKI 内核上的 root 解决方案，其 Manager 应用采用了 Jetpack Compose 构建，拥有精美的 Material 3 / Miuix 双主题系统和丰富的 UI 动效。

本项目在 KernelSU Manager 的基础上，**剥离了所有与内核、root 权限管理相关的功能代码**，仅保留了高质量的 UI 框架和可复用组件，使其成为一个干净的应用开发起点。

## 项目目的

提供一个**开箱即用的 Android 应用模板**，让开发者能够：

- 快速启动新项目，无需从零搭建 UI 框架
- 复用经过生产验证的 Material 3 + Miuix 双主题系统
- 利用现成的毛玻璃（Glassmorphism）、动画、导航等高级 UI 效果
- 专注于业务逻辑开发，而非基础设施搭建

## 技术栈

| 技术 | 说明 |
|------|------|
| **Jetpack Compose** | 声明式 UI 框架 |
| **Navigation3** | 最新的 Compose 导航方案 |
| **Material 3 + Miuix** | 双主题系统，可自由切换 |
| **Haze** | 毛玻璃 / 高斯模糊效果 |
| **Material Kolor** | 动态取色与调色板 |
| **OkHttp** | 网络请求 |

## 项目结构

```
app/src/main/java/com/hansyeoh/template/
├── data/                    # 数据层
│   └── repository/          # 设置仓储（SharedPreferences）
├── ui/
│   ├── component/           # 可复用 UI 组件
│   │   ├── animation/       # 动画组件
│   │   ├── bottombar/       # 底部导航栏（Material & Miuix）
│   │   ├── dialog/          # 对话框组件
│   │   ├── material/        # Material 风格组件
│   │   ├── miuix/           # Miuix 风格组件
│   │   └── statustag/       # 状态标签组件
│   ├── navigation3/         # 导航框架
│   ├── screen/              # 页面
│   │   ├── about/           # 关于页
│   │   ├── colorpalette/    # 主题调色板
│   │   ├── home/            # 主页（空）
│   │   ├── placeholder/     # 占位页（空）
│   │   └── settings/        # 设置页
│   ├── theme/               # 主题系统
│   ├── util/                # 工具类
│   └── viewmodel/           # ViewModel 层
└── TemplateApplication.kt   # Application 入口
```

## 内置功能

- **四栏底部导航**：主页、Tab 1、Tab 2、设置
- **设置页**：检查更新、UI 风格切换、主题/配色、发送日志、关于
- **双主题**：Material 3 和 Miuix 风格自由切换
- **深色模式**：跟随系统 / 浅色 / 深色
- **动态取色**：Monet 壁纸取色 + 手动调色板
- **毛玻璃效果**：顶栏和底栏高斯模糊
- **浮动底栏**：Apple 风格悬浮导航栏 + Liquid Glass

## 快速开始

1. 克隆项目
2. 用 Android Studio 打开
3. 修改 `com.hansyeoh.template` 为你自己的包名
4. 开始开发你的业务页面

## 构建

```bash
./gradlew :app:assembleDebug
```

## 许可证

本项目基于 [GPL-3.0](LICENSE) 许可证开源。

原始项目 [KernelSU](https://github.com/tiann/KernelSU) 由 [@tiann](https://github.com/tiann) 创建，同样基于 GPL-3.0 许可证。
