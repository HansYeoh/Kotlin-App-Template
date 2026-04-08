# RailTrace Kotlin Migration Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在保留 Kotlin/Compose 液态玻璃 UI 体系的前提下，把 Flutter `railtrace` 的核心业务能力迁移到当前 Android 工程，并优先保留现有 Appwrite 数据与车次查询能力。

**Architecture:** 不迁移 Flutter 的壳层、主题、认证 UI、Profile UI 和票面复刻 UI，而是把 `railtrace` 拆成 `domain/data/presentation` 三层后原生落到当前 `app + shared` 结构中。后端优先复用现有 Appwrite `user_tickets` 表和 `query-train-info` Function，避免数据二次搬迁。

**Tech Stack:** Jetpack Compose, Navigation3, ViewModel + StateFlow, Kotlin Coroutines, official Appwrite Android SDK, OkHttp, kotlinx.serialization, existing `shared` UI/theme/navigation components

---

## I'm using the writing-plans skill to create the implementation plan.

## First-Principles Summary

### Real goal

你的真实目标不是“把 Dart 改写成 Kotlin”，而是：

1. 保住 RailTrace 的核心价值。
2. 让它运行在你现在更喜欢的 Kotlin + 液态玻璃 UI 体系里。
3. 尽量少搬无关代码，尤其是不再喜欢或已经在 `shared` 里具备的那一层。

### Therefore

不应该做的事：

1. 不要按 Flutter 文件结构逐页翻译。
2. 不要先做包名大重构或工程级 rename。
3. 不要先移植 Flutter 的壳层能力，比如主题、登录 Branding、票面 1:1 UI。
4. 不要把 `core_template` 的整套认证/Profile/Feedback 全量搬过来。

应该做的事：

1. 先锁定 RailTrace 的“业务内核”。
2. 再用 Kotlin 原生方式重建这些能力。
3. 最大化复用现有 `shared` 和 `app` 的导航、主题、底栏、模糊、设置体系。

## Recommended Path

### Recommendation

推荐走“**保留 Appwrite 数据契约 + 原生重写业务层 + 复用 Kotlin UI 壳**”。

原因：

1. 这是最短路径，能最快拿到你真正想要的 Kotlin 视觉体验。
2. 复用现有 Appwrite 表结构和 `query-train-info` 云函数，可以避免再做一次后端迁移。
3. Flutter `railtrace` 本身很薄，真正需要迁的核心只有票据模型、查询逻辑、分组逻辑、导入逻辑和两个主要页面。

### Default assumption

本计划默认你**希望继续使用现有 Appwrite 数据**，即：

1. 继续读写 `railtrace_core.user_tickets`。
2. 继续调用 `query-train-info` Function。
3. 继续沿用“每个用户只看自己的票”的数据隔离逻辑。

如果你明确不需要历史数据和云同步，可以再切到“本地优先版本”。那会更快，但不是本计划的默认方案。

## Scope

### Must migrate

1. 行程页：未来行程列表、Hero 卡片、分组时间线、下拉刷新、编辑/删除。
2. 票夹页：月/年切换、月份/年份聚合、排序、分组时间线、详情入口。
3. 车票领域模型：`UserTicket`、`TicketGroup`、`TrainQueryResult`、邮件导入模型。
4. 新增/编辑行程：车次查询、站点选择、时间推导、字段校验、保存。
5. 邮件导入：正则解析、按 `(trainCode, date)` 去重查询、批量导入。
6. 数据层：Appwrite `Account`/`TablesDB`/`Functions` 最小接入，软删除与刷新逻辑。
7. 统计派生：待出行数、总票数、里程统计。
8. 本地化字符串与基础测试。

### Explicitly not migrated in v1

1. Flutter 的主题实现、Branding、shell、GoRouter 配置。
2. Flutter 票面复刻 UI：蓝票/红票切换属于 UI 呈现，不是核心业务。
3. `core_template` 的 Feedback、Onboarding、完整 Profile、完整 Auth UI。
4. 多端支持，当前只落 Android Kotlin。
5. 通知与生物锁。Flutter 版虽然初始化了这两项，但 RailTrace 业务本身当前没有强依赖它们。

## Key Decisions (ADR-lite)

### Decision 1: Backend contract stays unchanged

**Decision:** Kotlin 版继续使用 Flutter 版现有 Appwrite 表结构与函数契约。

**Why:**

1. 最小化迁移成本。
2. 可直接复用已有用户数据。
3. `query-train-info` 已经封装了 12306 查询、fallback 和缓存写入，不值得在客户端重做。

**Tradeoff:**

1. Kotlin 端仍会依赖 Appwrite。
2. 如果未来要离线优先或完全本地化，后续还要再抽象一次 repository。

### Decision 2: Rebuild business natively, do not transliterate Flutter UI

**Decision:** 只迁业务逻辑和交互语义，不复制 Flutter 组件结构和视觉。

**Why:**

1. 你的迁移动机本来就是想用 Kotlin 的液态玻璃 UI。
2. 逐组件翻译会把 Flutter 的历史包袱一并搬过来。
3. Compose 下可以直接复用 `shared` 的顶栏、底栏、模糊、主题能力。

### Decision 3: Delay package rename

**Decision:** 第一阶段先保留 `com.hansyeoh.template` 包结构和当前工程名，仅修改应用 branding。

**Why:**

1. 包名 rename 影响面大，但几乎不提升用户价值。
2. 先把业务功能跑通，后面再做低风险品牌整理。

### Decision 4: Auth only to the minimum necessary level

**Decision:** 如果必须读取现有 Appwrite 用户数据，就只接入“最小登录态能力”。

**Minimum means:**

1. 初始化 Appwrite client。
2. 恢复当前 session。
3. 获取当前 account。
4. 保证票据查询能拿到 `userId`。

**Not included in v1 unless you explicitly want it:**

1. OTP 完整流程。
2. Biometric lock。
3. Profile 编辑与 avatar。

## Target Kotlin Structure

### New package layout

建议新增而不是污染旧的 `placeholder`：

```text
app/src/main/java/com/hansyeoh/template/railtrace/
  data/
    appwrite/
    repository/
  domain/
    model/
    parser/
    usecase/
  presentation/
    trips/
    tickets/
    edit/
    importmail/
    component/
    state/
```

### Existing files to keep and adapt

1. `app/src/main/java/com/hansyeoh/template/ui/MainActivity.kt`
2. `app/src/main/java/com/hansyeoh/template/ui/navigation3/Routes.kt`
3. `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/*`
4. `app/src/main/java/com/hansyeoh/template/ui/viewmodel/MainActivityViewModel.kt`
5. `app/src/main/res/values/strings.xml`
6. `shared/src/main/java/com/hansyeoh/shared/**`

## Migration Phases

### Phase 0: Freeze contracts and success criteria

**Success criteria**

1. 能展示未来行程和历史票夹。
2. 能新增、编辑、软删除车票。
3. 能通过车次查询自动补站点/时间。
4. 能解析 12306 邮件并批量导入。
5. Kotlin UI 完全使用当前 `shared` 体系，不回退 Flutter 风格。

### Phase 1: Replace app shell with RailTrace shell

### Task 1: Convert template shell into RailTrace navigation shell

**Files:**
- Modify: `app/src/main/java/com/hansyeoh/template/ui/MainActivity.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/BottomBar.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/BottomBarMaterial.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/BottomBarMiuix.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/NavigationRailMaterial.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/component/bottombar/NavigationRailMiuix.kt`
- Modify: `app/src/main/res/values/strings.xml`

**Step 1: Replace the 4-tab placeholder pager with 3 pages**

Pages:
1. `Trips`
2. `Tickets`
3. `Settings`

**Step 2: Keep settings/about/theme routes unchanged**

Do not port Flutter shell route logic.

**Step 3: Rebrand visible strings**

Change app label and tab labels to RailTrace terms.

**Step 4: Smoke test**

Run:

```bash
./gradlew :app:assembleDebug
```

Expected:
1. App builds.
2. Bottom bar shows RailTrace tabs.
3. Existing settings page still works.

### Phase 2: Land the domain model first

### Task 2: Port pure domain logic before any network UI

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/domain/model/UserTicket.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/domain/model/TicketGroup.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/domain/model/TrainQueryResult.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/domain/model/TicketEnums.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/domain/parser/EmailTicketParser.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/domain/UserTicketTest.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/domain/TicketGroupTest.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/domain/TrainQueryResultTest.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/domain/EmailTicketParserTest.kt`

**Step 1: Write failing unit tests for pure logic**

Cover:
1. UTC/local datetime parsing.
2. Haversine distance.
3. group key generation.
4. multi-ticket grouping.
5. `TrainQueryResult` JSON parsing defaults.
6. email parsing for single and multi-passenger mail.

**Step 2: Implement immutable Kotlin models**

Use `data class` and small helper methods only.

**Step 3: Run unit tests**

Run:

```bash
./gradlew :app:testDebugUnitTest
```

Expected:
All new domain tests pass.

### Phase 3: Add Appwrite gateway with minimal auth dependency

### Task 3: Introduce native Appwrite access without porting Flutter auth UI

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/appwrite/RailTraceAppwriteConfig.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/appwrite/RailTraceAppwrite.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/TemplateApplication.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/repository/SessionRepository.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/repository/SessionRepositoryImpl.kt`

**Step 1: Add official Appwrite Android SDK**

Use the Android client SDK, not handwritten REST wrappers, unless release/proguard issues force fallback.

**Step 2: Initialize a singleton Appwrite client**

The singleton should expose at least:
1. `Account`
2. `TablesDB`
3. `Functions`

**Step 3: Add minimal session retrieval**

Need only:
1. restore current session
2. fetch current account
3. expose current `userId`

**Step 4: Add manifest callback activity only if OAuth is used**

If using email/password only, keep this deferred.

**Step 5: Smoke test against Appwrite sandbox/staging**

Expected:
1. App can initialize Appwrite.
2. Logged-in user can be resolved.
3. No RailTrace feature depends on Flutter `core_template`.

### Phase 4: Port repositories and state

### Task 4: Port RailTrace repository layer and view-state contracts

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/repository/TicketRepository.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/data/repository/AppwriteTicketRepository.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/state/TripsUiState.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/state/TicketsUiState.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/state/TripsViewModel.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/state/TicketsViewModel.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/data/TicketRepositoryMappingTest.kt`

**Step 1: Port repository operations**

Must include:
1. `getUpcomingTickets(userId)`
2. `getAllTickets(userId)`
3. `createTicket(data)`
4. `updateTicket(id, data)`
5. `softDeleteTicket(id)`
6. `queryTrainCache(trainCode, date)`
7. `fetchLiveServiceInfo(trainCode, date, depStationName)`

**Step 2: Preserve backend semantics**

Preserve:
1. `isDelete = false`
2. `arrTime > now` for upcoming
3. sort by `depTime`
4. `query-train-info` fallback behavior

**Step 3: Keep business logic out of Compose**

Parsing, grouping, refresh, and delete flow should live in repository/viewmodel, not composables.

**Step 4: Verify repository mapping**

Expected:
1. Kotlin model field names still align with Appwrite column names.
2. `snapshotJson` remains round-trippable.
3. point fields still map as `[lon, lat]`.

### Phase 5: Build Trips first, because it is the core daily-use surface

### Task 5: Implement Trips screen and add/edit entry points

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/trips/TripsScreen.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/trips/TripHeroCard.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/component/UnifiedTimeline.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/component/TicketTimelineCard.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/MainActivity.kt`

**Step 1: Implement Trips tab using current Kotlin top-bar/bottom-bar system**

Keep the structure:
1. Hero card for nearest future trip
2. grouped timeline below
3. pull-to-refresh
4. add/edit/delete entry points

**Step 2: Do not replicate Flutter visuals**

Use current Compose + `shared` visual language.

**Step 3: Port live waiting-room / gate enrichment**

Only fetch live info for same-day trips, matching Flutter behavior.

**Step 4: Verify interactions**

Expected:
1. refresh updates list
2. delete uses soft delete
3. edit opens form with prefilled values
4. group tickets still show as one trip block

### Phase 6: Build Tickets wallet next

### Task 6: Implement wallet, month/year aggregation, and details

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/tickets/TicketsScreen.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/tickets/TicketCalendarNavigator.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/tickets/TicketDetailScreen.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/presentation/TicketGroupingViewModelTest.kt`

**Step 1: Port month/year grouping logic**

Preserve:
1. month aggregation
2. year aggregation
3. total count
4. total price
5. asc/desc sorting

**Step 2: Replace the fake paper-ticket page with native details**

Ticket detail should show:
1. route
2. time
3. seat
4. passenger
5. gate/waiting room
6. memo
7. edit/delete actions

**Step 3: Verify wallet navigation**

Expected:
1. switching month/year changes grouping
2. current page index remains valid after refresh
3. ticket detail opens from wallet entries

### Phase 7: Port add/edit flow

### Task 7: Implement add/edit trip form and train query workflow

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/edit/EditTripScreen.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/edit/EditTripViewModel.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/presentation/EditTripViewModelTest.kt`

**Step 1: Rebuild form as a native Compose flow**

Sections:
1. train query
2. station selection
3. time selection
4. required seat info
5. optional metadata

**Step 2: Preserve business rules**

Must preserve:
1. empty train code validation
2. departure before arrival validation
3. station order validation
4. arrival day offset calculation
5. manual fallback when query data is absent

**Step 3: Prefill from existing `snapshotJson`**

Edit flow must reconstruct station indices and times when possible.

**Step 4: Verify create/update**

Expected:
1. query populates train metadata
2. create writes full record
3. update preserves document id and refreshes screens

### Phase 8: Port email import

### Task 8: Implement email import, enrichment, and bulk import

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/importmail/EmailImportScreen.kt`
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/importmail/EmailImportViewModel.kt`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/presentation/EmailImportViewModelTest.kt`

**Step 1: Port parser-backed import flow**

Flow:
1. paste email text
2. parse tickets
3. group by `(trainCode, date)` for enrichment cache
4. preview selected tickets
5. batch import

**Step 2: Preserve fallback semantics**

Keep the same meaning of:
1. matched date
2. fallback used
3. no-match warning

**Step 3: Verify bulk import**

Expected:
1. selected subset imports correctly
2. duplicate query requests are avoided
3. import refreshes trips and tickets views

### Phase 9: Derived stats and shell polish

### Task 9: Reintroduce only the useful shell integrations

**Files:**
- Create: `app/src/main/java/com/hansyeoh/template/railtrace/presentation/state/UserStats.kt`
- Modify: `app/src/main/java/com/hansyeoh/template/ui/MainActivity.kt`
- Modify: `app/src/main/res/values/strings.xml`

**Step 1: Add derived stats provider/viewmodel**

Derived from repository-backed states:
1. pending trips
2. ticket count
3. mileage

**Step 2: Integrate into whichever Kotlin shell surface is most natural**

Do not force a Flutter-like profile page if the Android app does not need one yet.

**Step 3: Add offline indicator only if cheap**

This is a “nice to have”, not a blocker.

### Phase 10: Hardening and release readiness

### Task 10: Add tests, strings, and release checks

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values-zh-rCN/strings_railtrace.xml`
- Create: `app/src/test/java/com/hansyeoh/template/railtrace/**`
- Modify: `app/proguard-rules.pro`

**Step 1: Expand test coverage**

Must cover:
1. date parsing
2. email parsing
3. grouping
4. repository mapping
5. form validation
6. arrival-day rollover

**Step 2: Release-build validation**

Run:

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Expected:
1. Appwrite SDK does not break release build.
2. R8/proguard rules are sufficient.
3. No missing string resources.

## Priority Order

### Build in this order

1. Shell and branding
2. Pure domain logic
3. Appwrite client and repository
4. Trips page
5. Tickets page
6. Add/edit form
7. Email import
8. Stats polish
9. Hardening

### Why this order

1. `Trips` is the highest-value daily surface.
2. `Tickets` depends on the same data layer, so it becomes cheaper after Trips.
3. Add/edit and email import should land after repository contracts are stable.

## Fastest Viable Slice

如果你要最快看到“Kotlin RailTrace 已经活了”，最小可演示版本应该只做：

1. Trips tab
2. Tickets tab
3. Appwrite user_tickets read
4. add/edit trip
5. soft delete

邮件导入和 live gate enrichment 可以排到第二波。

## Risks

### Risk 1: Auth requirement is stronger than it looks

If existing data is tied to authenticated Appwrite users, then “只迁 RailTrace 业务”仍然至少需要一个最小登录态。

**Mitigation:**
1. 先确认你是否必须读取现有用户数据。
2. 如果必须，先做 session restore + current account，不做完整 Flutter auth UI 迁移。

### Risk 2: Appwrite Android release compatibility

The Android SDK may require release verification and potential R8/proguard adjustments.

**Mitigation:**
1. 在 Phase 3 就跑 `assembleRelease`。
2. 早发现依赖问题，避免功能做完后才卡壳。

### Risk 3: Over-migrating UI details

最容易浪费时间的是复刻 Flutter 的票面和卡片视觉。

**Mitigation:**
把“功能语义”与“视觉复刻”分开，v1 只做前者。

## What I would do if optimizing purely for your stated goal

如果我是按“最快达成你想要的 Kotlin 液态玻璃体验”来排，我会这样执行：

1. 先不改 namespace。
2. 先不做完整 auth/profile。
3. 先把 Trips/Tickets/Add-Edit 三块跑起来。
4. 直接复用 Appwrite 后端。
5. 邮件导入作为第二阶段。

这是比“全面迁移 Flutter railtrace”更短、更稳、也更符合你动机的路径。

## Open decision that changes the plan

只有一个决策会显著改变路线：

**你是否必须继续使用 Flutter 版现有 Appwrite 账号和历史数据？**

1. 如果必须，本计划保持不变。
2. 如果不必须，我建议改成“本地优先 + 导入历史数据”的更短路径，复杂度会再降一档。

