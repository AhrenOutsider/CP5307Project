package com.example.cp5307project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cp5307project.ui.theme.CP5307ProjectTheme

// ========================
// 颜色常量（统一管理）
// ========================
private val Orange = Color(0xFFFF9800)
private val OrangeIndicator = Color(0xFFFFE8C8)
private val GrayText = Color(0xFF666666)

// ========================
// Activity & App Scaffold
// ========================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP5307ProjectTheme {
                CP5307ProjectApp()
            }
        }
    }
}

@Composable
fun CP5307ProjectApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { dest ->
                    val selected = dest == currentDestination

                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = dest },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = OrangeIndicator,
                            selectedIconColor = Orange,
                            selectedTextColor = Orange,
                            unselectedIconColor = GrayText,
                            unselectedTextColor = GrayText
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen(Modifier.padding(innerPadding))
            AppDestinations.LEARNING -> LearningScreen(Modifier.padding(innerPadding))
            AppDestinations.SEARCH -> NavigationSearchScreen(Modifier.padding(innerPadding))
            AppDestinations.SETTINGS -> SettingsScreen(Modifier.padding(innerPadding))
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    LEARNING("Learning", Icons.Default.Star),
    SEARCH("Search", Icons.Default.Search),
    SETTINGS("Setting", Icons.Default.Settings),
}

// ========================
// Home (with Subject jump)
// ========================
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var selectedSubject by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllSubjects by rememberSaveable { mutableStateOf(false) }
    var showPromo by rememberSaveable { mutableStateOf(false) }   // ✅ 新增：促销页开关

    // ✅ 0) Promotion 页面（最优先）
    if (showPromo) {
        PromoCoursesScreen(
            modifier = modifier,
            onBack = { showPromo = false }
        )
        return
    }

    // ✅ 1) Subject -> Courses page
    if (selectedSubject != null) {
        CoursesBySubjectScreen(
            subject = selectedSubject!!,
            modifier = modifier,
            onBack = { selectedSubject = null }
        )
        return
    }

    // ✅ 2) View all subjects page
    if (showAllSubjects) {
        SubjectViewAllScreen(
            modifier = modifier,
            onBack = { showAllSubjects = false },
            onSelectSubject = { selectedSubject = it }
        )
        return
    }

    // ✅ 3) Home main page
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(16.dp))

        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 关键：把点击传进 Banner
        BannerCard(
            onLearnMoreClick = { showPromo = true }
        )
        Spacer(modifier = Modifier.height(24.dp))

        SubjectSection(
            onSubjectClick = { selectedSubject = it },
            onViewAllClick = { showAllSubjects = true }
        )
        Spacer(modifier = Modifier.height(24.dp))

        PopularSection()
        Spacer(modifier = Modifier.height(24.dp))

        RecommendedSection()
    }
}

@Composable
fun TopBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Explore", style = MaterialTheme.typography.headlineMedium)
        Row {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
        }
    }
}

@Composable
fun SearchBar() {
    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Search") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun BannerCard(onLearnMoreClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.banner),
                contentDescription = "Sale Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Sale For This Year!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onLearnMoreClick,  // ✅ 点这里跳
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Learn more")
                    }
                }
            }
        }
    }
}

// ========================
// Subject section + View All
// ========================
@Composable
fun SubjectSection(
    onSubjectClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Subject", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onViewAllClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) { Text("View All") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            item {
                SubjectBox(
                    title = "AI",
                    imageRes = R.drawable.ai,
                    onClick = { onSubjectClick("AI") }
                )
            }

            item {
                SubjectBox(
                    title = "Machine Learning",
                    imageRes = R.drawable.ml,
                    onClick = { onSubjectClick("Machine Learning") }
                )
            }

            item {
                SubjectBox(
                    title = "Deep Learning",
                    imageRes = R.drawable.dl,
                    onClick = { onSubjectClick("Deep Learning") }
                )
            }

            item {
                SubjectBox(
                    title = "Coding",
                    imageRes = R.drawable.coding,
                    onClick = { onSubjectClick("Coding") }
                )
            }
        }
    }
}

@Composable
fun SubjectBox(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(vertical = 6.dp, horizontal = 6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SubjectViewAllScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSelectSubject: (String) -> Unit
) {
    val subjects = listOf(
        "AI" to R.drawable.ai,
        "Machine Learning" to R.drawable.ml,
        "Deep Learning" to R.drawable.dl,
        "Coding" to R.drawable.coding
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "All Subjects",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(subjects) { (name, iconRes) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSubject(name) },
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = name,
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }
        }
    }
}

// ========================
// Courses by Subject
// ========================
data class CourseItem(val title: String, val subtitle: String, val imageRes: Int)

// ========================
// Courses by Subject（支持点击进入观看页面）
// ========================

@Composable
fun CoursesBySubjectScreen(
    subject: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {

    // 选中的课程（用于跳转）
    var selectedCourse by rememberSaveable { mutableStateOf<CourseItem?>(null) }

    // 根据 subject 生成课程列表
    val courses = remember(subject) {
        when (subject) {
            "AI" -> listOf(
                CourseItem("AI Basics", "Intro • Beginner", R.drawable.popular1),
                CourseItem("AI in Daily Life", "Examples • Beginner", R.drawable.popular2)
            )
            "Machine Learning" -> listOf(
                CourseItem("ML Fundamentals", "Theory • Beginner", R.drawable.popular2),
                CourseItem("Regression & Classification", "Practice • Beginner", R.drawable.popular3)
            )
            "Deep Learning" -> listOf(
                CourseItem("Neural Networks 101", "DL • Beginner", R.drawable.popular3),
                CourseItem("CNN Basics", "Vision • Beginner", R.drawable.popular4)
            )
            else -> listOf(
                CourseItem("Kotlin Basics", "Coding • Beginner", R.drawable.popular1),
                CourseItem("Compose UI", "Android • Beginner", R.drawable.popular2)
            )
        }
    }

    // 如果已经选中课程 → 进入观看页面
    if (selectedCourse != null) {
        CourseWatchScreen(
            course = selectedCourse!!,
            onBack = { selectedCourse = null }
        )
        return
    }

    // 正常课程列表页面
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = subject,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Courses",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(courses) { c ->
                CourseCard(
                    course = c,
                    onClick = { selectedCourse = c }
                )
            }
        }
    }
}


// ========================
// Course Card（可点击）
// ========================

@Composable
fun CourseCard(
    course: CourseItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(course.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}


// ========================
// 观看课程页面
// ========================

@Composable
fun CourseWatchScreen(
    course: CourseItem,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(course.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = course.imageRes),
            contentDescription = course.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(course.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* 播放逻辑 */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Watching")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Lesson List",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        repeat(5) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lesson ${index + 1}",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

// ========================
// Popular & Recommended (unchanged)
// ========================
@Composable
fun PopularSection() {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Popular", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) { Text("View All") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val popularList = listOf(
            "Course Name" to R.drawable.popular1,
            "Course Name" to R.drawable.popular2,
            "Course Name" to R.drawable.popular3,
            "Course Name" to R.drawable.popular4
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(popularList) { (title, image) ->
                PopularBox(title = title, imageRes = image)
            }
        }
    }
}

@Composable
fun PopularBox(title: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(10.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RecommendedSection() {
    Column {
        Text("Recommended For You", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RecommendationItem("XXXXXXXXXXXXXXXXXXXXX")
            RecommendationItem("XXXXXXXXXXXXXXXXXXXXX")
            RecommendationItem("XXXXXXXXXXXXXXXXXXXXX")
        }
    }
}

@Composable
fun RecommendationItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ========================
// Search page
// ========================
@Composable
fun NavigationSearchScreen(modifier: Modifier = Modifier) {
    var query by rememberSaveable { mutableStateOf("") }

    val allItems = remember {
        listOf(
            "Android Studio",
            "Jetpack Compose",
            "Kotlin Basics",
            "Navigation Bar",
            "LazyRow / LazyColumn",
            "Material 3",
            "Room Database",
            "Retrofit Networking",
            "Firebase Login",
            "Coroutines",
            "ViewModel",
            "UI Design"
        )
    }

    val results = remember(query) {
        if (query.isBlank()) allItems else allItems.filter { it.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Search", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Type to search...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Results: ${results.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(results) { item -> SearchResultCard(title = item) }
        }
    }
}

@Composable
fun SearchResultCard(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ========================
// Learning (My courses + detail + progress saved)
// ========================
data class LearningCourse(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val lessonsTotal: Int
)

@Composable
fun LearningScreen(modifier: Modifier = Modifier) {

    // 固定课程列表（你可以换成真实数据）
    val courses = remember {
        listOf(
            LearningCourse(
                id = "compose",
                title = "Jetpack Compose Basics",
                subtitle = "UI • Beginner",
                imageRes = R.drawable.course_compose,
                lessonsTotal = 20
            ),
            LearningCourse(
                id = "kotlin",
                title = "Kotlin for Android",
                subtitle = "Language • Beginner",
                imageRes = R.drawable.course_kotlin,
                lessonsTotal = 26
            ),
            LearningCourse(
                id = "m3",
                title = "Material 3 Design",
                subtitle = "Design • Intermediate",
                imageRes = R.drawable.course_m3,
                lessonsTotal = 11
            )
        )
    }

    // 进度保存：courseId -> lessonsDone
    val progressMapSaver = mapSaver(
        save = { state: MutableMap<String, Int> -> state.toMap() },
        restore = { restored ->
            restored.mapValues { it.value as Int }.toMutableMap()
        }
    )

    var lessonsDoneMap by rememberSaveable(stateSaver = progressMapSaver) {
        mutableStateOf(
            mutableMapOf(
                "compose" to 7,
                "kotlin" to 16,
                "m3" to 2
            )
        )
    }

    // 简单详情页切换（不使用 Navigation 组件也能“跳转”）
    var selectedCourseId by rememberSaveable { mutableStateOf<String?>(null) }

    // 进入详情页
    if (selectedCourseId != null) {
        val course = courses.first { it.id == selectedCourseId }
        val done = lessonsDoneMap[course.id] ?: 0

        LearningDetailScreen(
            course = course,
            lessonsDone = done,
            onBack = { selectedCourseId = null },
            onCompleteNextLesson = {
                val current = lessonsDoneMap[course.id] ?: 0
                if (current < course.lessonsTotal) {
                    lessonsDoneMap = lessonsDoneMap.toMutableMap().apply {
                        this[course.id] = current + 1
                    }
                }
            },
            onResetProgress = {
                lessonsDoneMap = lessonsDoneMap.toMutableMap().apply {
                    this[course.id] = 0
                }
            }
        )
        return
    }

    // 分组：进行中 / 已完成
    val inProgress = courses.filter { (lessonsDoneMap[it.id] ?: 0) < it.lessonsTotal }
    val completed = courses.filter { (lessonsDoneMap[it.id] ?: 0) >= it.lessonsTotal }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("My Learning", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Continue where you left off", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item { Text("In Progress", style = MaterialTheme.typography.titleMedium) }

        items(inProgress) { course ->
            val done = lessonsDoneMap[course.id] ?: 0
            val progress = done.toFloat() / course.lessonsTotal.toFloat()

            LearningCourseCard(
                course = course,
                lessonsDone = done,
                progress = progress,
                onContinue = { selectedCourseId = course.id }
            )
        }

        if (completed.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Completed", style = MaterialTheme.typography.titleMedium)
            }

            items(completed) { course ->
                LearningCompletedCard(
                    course = course,
                    onOpen = { selectedCourseId = course.id }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
fun LearningCourseCard(
    course: LearningCourse,
    lessonsDone: Int,
    progress: Float,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color(0xFFFFE8C8), RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(course.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$lessonsDone/${course.lessonsTotal} lessons",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onContinue,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun LearningCompletedCard(
    course: LearningCourse,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                modifier = Modifier.size(62.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Completed", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun LearningDetailScreen(
    course: LearningCourse,
    lessonsDone: Int,
    onBack: () -> Unit,
    onCompleteNextLesson: () -> Unit,
    onResetProgress: () -> Unit
) {
    val progress = lessonsDone.toFloat() / course.lessonsTotal.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(course.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = course.imageRes),
            contentDescription = course.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(course.subtitle, color = Color.Gray)

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Text("Progress: $lessonsDone/${course.lessonsTotal} lessons", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCompleteNextLesson,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            )
        ) {
            Text("Complete Next Lesson")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onResetProgress,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Reset Progress")
        }
    }
}

// ========================
// Settings (clean structure)
// ========================
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    var notifications by rememberSaveable { mutableStateOf(true) }
    var darkMode by rememberSaveable { mutableStateOf(false) }
    var wifiOnly by rememberSaveable { mutableStateOf(true) }
    var autoplay by rememberSaveable { mutableStateOf(true) }

    var language by rememberSaveable { mutableStateOf("English") }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        SettingsAccountCard()

        Spacer(modifier = Modifier.height(20.dp))

        SettingsGroupTitle("General")

        SettingsSwitchRow(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Receive reminders and updates",
            checked = notifications,
            onCheckedChange = { notifications = it }
        )

        SettingsSwitchRow(
            icon = Icons.Default.DarkMode,
            title = "Dark Mode",
            subtitle = "Reduce eye strain at night",
            checked = darkMode,
            onCheckedChange = { darkMode = it }
        )

        SettingsActionRow(
            icon = Icons.Default.Language,
            title = "Language",
            subtitle = language,
            onClick = { showLanguageDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroupTitle("Learning")

        SettingsSwitchRow(
            icon = Icons.Default.PlayArrow,
            title = "Autoplay next lesson",
            subtitle = "Play next content automatically",
            checked = autoplay,
            onCheckedChange = { autoplay = it }
        )

        SettingsSwitchRow(
            icon = Icons.Default.Wifi,
            title = "Wi-Fi only downloads",
            subtitle = "Save mobile data usage",
            checked = wifiOnly,
            onCheckedChange = { wifiOnly = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroupTitle("More")

        SettingsActionRow(
            icon = Icons.Default.Delete,
            title = "Clear cache",
            subtitle = "Free up storage space",
            onClick = { /* TODO */ }
        )

        SettingsActionRow(
            icon = Icons.Default.Info,
            title = "About",
            subtitle = "Version 0.0.1",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log out")
        }
    }

    if (showLanguageDialog) {
        LanguageDialog(
            current = language,
            onDismiss = { showLanguageDialog = false },
            onSelect = {
                language = it
                showLanguageDialog = false
            }
        )
    }
}

@Composable
private fun SettingsAccountCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Username", style = MaterialTheme.typography.titleMedium)
                Text("Username@example.com", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun SettingsGroupTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun LanguageDialog(
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val options = listOf("English", "中文", "日本語", "한국어")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                options.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang, modifier = Modifier.weight(1f))
                        if (lang == current) {
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
data class PromoCourse(
    val title: String,
    val subtitle: String,
    val priceNow: String,
    val priceOld: String,
    val imageRes: Int
)

@Composable
fun PromoCoursesScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val promoCourses = remember {
        listOf(
            PromoCourse("Compose Masterclass", "UI • Beginner", "S$9.90", "S$49.90", R.drawable.popular1),
            PromoCourse("Kotlin Fast Track", "Coding • Beginner", "S$12.90", "S$59.90", R.drawable.popular2),
            PromoCourse("AI Starter Pack", "AI • Beginner", "S$15.90", "S$79.90", R.drawable.popular3),
            PromoCourse("ML Crash Course", "ML • Beginner", "S$18.90", "S$89.90", R.drawable.popular4),
        )
    }

    var selected by rememberSaveable { mutableStateOf<PromoCourse?>(null) }

    if (selected != null) {
        PromoDetailDialog(
            course = selected!!,
            onDismiss = { selected = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Promotion",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Limited-time offers",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(promoCourses) { c ->
                PromoCourseCard(course = c, onClick = { selected = c })
            }
        }
    }
}

@Composable
fun PromoCourseCard(course: PromoCourse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(course.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(course.priceNow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        course.priceOld,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun PromoDetailDialog(course: PromoCourse, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.title) },
        text = {
            Column {
                Text(course.subtitle, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Promo Price: ${course.priceNow}", fontWeight = FontWeight.Bold)
                Text("Original: ${course.priceOld}", color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                Text("This is a limited-time offer course. (Demo info)")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}