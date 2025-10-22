package com.cobrien.myday

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI
import java.time.LocalTime

@Composable
fun GreetingWidget() {
    val currentTime = remember { LocalTime.now() }
    val greeting = when (currentTime.hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun QuoteWidget() {
    val quotes = remember {
        listOf(
            "The only way to do great work is to love what you do." to "Steve Jobs",
            "Success is not final, failure is not fatal: it is the courage to continue that counts." to "Winston Churchill",
            "Believe you can and you're halfway there." to "Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
            "It does not matter how slowly you go as long as you do not stop." to "Confucius",
            "Everything you've ever wanted is on the other side of fear." to "George Addair",
            "Believe in yourself. You are braver than you think, more talented than you know, and capable of more than you imagine." to "Roy T. Bennett",
            "I learned that courage was not the absence of fear, but the triumph over it." to "Nelson Mandela",
            "There is only one way to avoid criticism: do nothing, say nothing, and be nothing." to "Aristotle",
            "Do what you can with all you have, wherever you are." to "Theodore Roosevelt",
            "Everyone has a photographic memory, some just don't have film." to "Steven Wright"
        )
    }
    
    val (quote, author) = remember { quotes.random() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💭 Quote of the Day",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— $author",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

data class NewsArticle(
    val title: String,
    val description: String,
    val source: String,
    val url: String
)

// News cache data class
private data class NewsCacheEntry(
    val articles: List<NewsArticle>,
    val timestamp: Long,
    val category: String,
    val source: String
)

// Simple in-memory cache for news articles
private object NewsCache {
    private val cache = mutableMapOf<String, NewsCacheEntry>()
    private const val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes

    private fun getCacheKey(category: String, source: String): String {
        return "${source.uppercase()}_${category.lowercase()}"
    }

    fun get(category: String, source: String): List<NewsArticle>? {
        val key = getCacheKey(category, source)
        val entry = cache[key] ?: return null

        val age = System.currentTimeMillis() - entry.timestamp
        return if (age < CACHE_DURATION_MS) {
            entry.articles
        } else {
            // Cache expired, remove it
            cache.remove(key)
            null
        }
    }

    fun put(category: String, source: String, articles: List<NewsArticle>) {
        val key = getCacheKey(category, source)
        cache[key] = NewsCacheEntry(
            articles = articles,
            timestamp = System.currentTimeMillis(),
            category = category,
            source = source
        )
    }

    fun clear() {
        cache.clear()
    }

    fun getCacheAge(category: String, source: String): Long? {
        val key = getCacheKey(category, source)
        val entry = cache[key] ?: return null
        return System.currentTimeMillis() - entry.timestamp
    }
}

@Composable
fun NewsWidget(category: String, source: String = "BBC") {
    var newsArticles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Load news on initial composition and when refreshTrigger, category, or source changes
    LaunchedEffect(category, source, refreshTrigger) {
        // Check cache first
        val cachedArticles = NewsCache.get(category, source)
        if (cachedArticles != null && refreshTrigger == 0) {
            // Use cached data
            newsArticles = cachedArticles
            isLoading = false
        } else {
            // Fetch fresh data
            isLoading = true
            error = null
            try {
                val fetchedArticles = fetchNews(category, source)
                newsArticles = fetchedArticles
                // Store in cache
                NewsCache.put(category, source, fetchedArticles)
            } catch (e: Exception) {
                error = "Failed to load news"
                newsArticles = getDemoNews(category)
            } finally {
                isLoading = false
            }
        }
    }

    // Auto-refresh every 30 minutes
    LaunchedEffect(category) {
        kotlinx.coroutines.delay(30 * 60 * 1000L) // Wait 30 minutes before first auto-refresh
        while (true) {
            refreshTrigger++
            kotlinx.coroutines.delay(30 * 60 * 1000L) // 30 minutes
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📰 News Headlines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                error != null && newsArticles.isEmpty() -> {
                    Text(
                        text = error ?: "Failed to load news",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    newsArticles.take(5).forEach { article ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(URI(article.url))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        .pointerHoverIcon(PointerIcon.Hand),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (article.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = article.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = article.source,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open link",
                            modifier = Modifier.size(18.dp).padding(top = 2.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                        if (article != newsArticles.take(5).last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

// News fetching and parsing functions
private suspend fun fetchNews(category: String, source: String = "BBC"): List<NewsArticle> {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            // Get RSS URL based on source and category
            val rssUrl = getRSSUrl(source, category)

            val url = java.net.URL(rssUrl)
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val response = connection.getInputStream().bufferedReader().use { it.readText() }

            // Check if it's an Atom feed (used by The Verge) or RSS
            val articlesList = if (response.contains("<feed") && response.contains("xmlns=\"http://www.w3.org/2005/Atom\"")) {
                parseAtomFeed(response)
            } else {
                parseStandardRSS(response)
            }

            if (articlesList.isNotEmpty()) {
                return@withContext articlesList
            }

            // Fallback to demo data
            getDemoNews(category)
        } catch (e: Exception) {
            e.printStackTrace()
            getDemoNews(category)
        }
    }
}

private fun getRSSUrl(source: String, category: String): String {
    return when(source.uppercase()) {
        "BBC" -> when(category.lowercase()) {
            "world" -> "https://feeds.bbci.co.uk/news/world/rss.xml"
            "business" -> "https://feeds.bbci.co.uk/news/business/rss.xml"
            "technology" -> "https://feeds.bbci.co.uk/news/technology/rss.xml"
            "science" -> "https://feeds.bbci.co.uk/news/science_and_environment/rss.xml"
            "health" -> "https://feeds.bbci.co.uk/news/health/rss.xml"
            "sports" -> "https://feeds.bbci.co.uk/sport/rss.xml"
            "entertainment" -> "https://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml"
            else -> "https://feeds.bbci.co.uk/news/rss.xml"
        }
        "CNN" -> "http://rss.cnn.com/rss/cnn_topstories.rss" // CNN only has general feed
        "NPR" -> when(category.lowercase()) {
            "world" -> "https://feeds.npr.org/1004/rss.xml"
            "business" -> "https://feeds.npr.org/1006/rss.xml"
            "technology" -> "https://feeds.npr.org/1019/rss.xml"
            "science" -> "https://feeds.npr.org/1007/rss.xml"
            "health" -> "https://feeds.npr.org/1128/rss.xml"
            "sports" -> "https://feeds.npr.org/1055/rss.xml"
            else -> "https://feeds.npr.org/1001/rss.xml" // All things considered
        }
        "THE VERGE" -> "https://www.theverge.com/rss/index.xml" // Tech/Science
        "ARS TECHNICA" -> "https://feeds.arstechnica.com/arstechnica/index" // Tech/Science
        "ENGADGET" -> "https://www.engadget.com/rss.xml" // Tech only
        "TECHCRUNCH" -> "https://techcrunch.com/feed/" // Tech only
        "WIRED" -> "https://www.wired.com/feed/rss" // Tech/Science focused
        else -> "https://feeds.bbci.co.uk/news/rss.xml" // Default to BBC
    }
}

private fun parseStandardRSS(rssXml: String): List<NewsArticle> {
    val articles = mutableListOf<NewsArticle>()

    try {
        // Standard RSS feed parsing
        val itemPattern = "<item>(.*?)</item>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val titlePattern = "<title><!\\[CDATA\\[(.*?)\\]\\]></title>".toRegex()
        val titlePlainPattern = "<title>(.*?)</title>".toRegex()
        val linkCDATAPattern = "<link><!\\[CDATA\\[(.*?)\\]\\]></link>".toRegex()
        val linkPattern = "<link>(.*?)</link>".toRegex()
        val descPattern = "<description><!\\[CDATA\\[(.*?)\\]\\]></description>".toRegex()
        val descPlainPattern = "<description>(.*?)</description>".toRegex()

        val items = itemPattern.findAll(rssXml).take(5)

        for (item in items) {
            val itemText = item.groupValues[1]

            // Extract title (try CDATA first, fallback to plain)
            val title = (titlePattern.find(itemText)?.groupValues?.get(1)
                ?: titlePlainPattern.find(itemText)?.groupValues?.get(1))?.trim() ?: ""

            // Extract link - direct article URL (try CDATA first, fallback to plain)
            val link = (linkCDATAPattern.find(itemText)?.groupValues?.get(1)
                ?: linkPattern.find(itemText)?.groupValues?.get(1))?.trim() ?: ""

            // Extract description (try CDATA first, fallback to plain)
            val desc = (descPattern.find(itemText)?.groupValues?.get(1)
                ?: descPlainPattern.find(itemText)?.groupValues?.get(1))?.trim() ?: ""

            if (title.isNotBlank() && link.isNotBlank()) {
                // Clean up description (remove HTML tags)
                val cleanDesc = desc.replace("<.*?>".toRegex(), "").trim()

                // Extract source from link domain
                val source = try {
                    val urlObj = java.net.URL(link)
                    urlObj.host.replace("www.", "").split(".").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "News"
                } catch (e: Exception) {
                    "News"
                }

                articles.add(
                    NewsArticle(
                        title = title,
                        description = cleanDesc.take(200),
                        source = source,
                        url = link
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return articles
}

private fun parseAtomFeed(atomXml: String): List<NewsArticle> {
    val articles = mutableListOf<NewsArticle>()

    try {
        // Atom feed parsing (used by The Verge)
        val entryPattern = "<entry>(.*?)</entry>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val titlePattern = "<title.*?><!\\[CDATA\\[(.*?)\\]\\]></title>".toRegex()
        val titlePlainPattern = "<title.*?>(.*?)</title>".toRegex()
        val linkPattern = "<link rel=\"alternate\".*?href=\"(.*?)\"".toRegex()
        val summaryPattern = "<summary.*?><!\\[CDATA\\[(.*?)\\]\\]></summary>".toRegex()
        val summaryPlainPattern = "<summary.*?>(.*?)</summary>".toRegex()

        val entries = entryPattern.findAll(atomXml).take(5)

        for (entry in entries) {
            val entryText = entry.groupValues[1]

            // Extract title (try CDATA first, fallback to plain)
            val title = (titlePattern.find(entryText)?.groupValues?.get(1)
                ?: titlePlainPattern.find(entryText)?.groupValues?.get(1))?.trim() ?: ""

            // Extract link from Atom format: <link rel="alternate" href="url" />
            val link = linkPattern.find(entryText)?.groupValues?.get(1)?.trim() ?: ""

            // Extract summary/description (try CDATA first, fallback to plain)
            val desc = (summaryPattern.find(entryText)?.groupValues?.get(1)
                ?: summaryPlainPattern.find(entryText)?.groupValues?.get(1))?.trim() ?: ""

            if (title.isNotBlank() && link.isNotBlank()) {
                // Clean up description (remove HTML tags)
                val cleanDesc = desc.replace("<.*?>".toRegex(), "").trim()

                // Extract source from link domain
                val source = try {
                    val urlObj = java.net.URL(link)
                    urlObj.host.replace("www.", "").split(".").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "News"
                } catch (e: Exception) {
                    "News"
                }

                articles.add(
                    NewsArticle(
                        title = title,
                        description = cleanDesc.take(200),
                        source = source,
                        url = link
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return articles
}

private fun parseGoogleNewsRSS(rssXml: String): List<NewsArticle> {
    val articles = mutableListOf<NewsArticle>()

    try {
        // Simple XML parsing for RSS feed using regex
        val itemPattern = "<item>(.*?)</item>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val titlePattern = "<title><!\\[CDATA\\[(.*?)\\]\\]></title>".toRegex()
        val titlePlainPattern = "<title>(.*?)</title>".toRegex()
        val descPattern = "<description><!\\[CDATA\\[(.*?)\\]\\]></description>".toRegex()
        val sourcePattern = "<source.*?>(.*?)</source>".toRegex()

        val items = itemPattern.findAll(rssXml).take(5)

        for (item in items) {
            val itemText = item.groupValues[1]

            // Try CDATA title first, fallback to plain title
            val title = (titlePattern.find(itemText)?.groupValues?.get(1)
                ?: titlePlainPattern.find(itemText)?.groupValues?.get(1))?.trim() ?: ""

            val desc = descPattern.find(itemText)?.groupValues?.get(1)?.trim() ?: ""
            val source = sourcePattern.find(itemText)?.groupValues?.get(1)?.trim() ?: "Google News"

            // Extract the actual article URL from the first <a href> in the description
            // The description contains HTML like: <a href="https://actualsite.com/article">...
            val actualUrlPattern = "<a\\s+href=\"(https?://[^\"]+)\"".toRegex()
            val actualUrl = actualUrlPattern.find(desc)?.groupValues?.get(1)?.trim() ?: ""

            if (title.isNotBlank() && actualUrl.isNotBlank()) {
                // Extract plain text from description (first article summary)
                // Description format: <ol><li><a href="...">Title</a>&nbsp;&nbsp;<font>Source</font></li>...
                val descTextPattern = "target=\"_blank\">([^<]+)</a>".toRegex()
                val descText = descTextPattern.find(desc)?.groupValues?.get(1)?.trim() ?: ""

                // Get a preview - use the article title or extract text before source
                val preview = if (descText.isNotBlank() && descText != title) {
                    descText.take(200)
                } else {
                    "Click to read the full article from $source"
                }

                articles.add(
                    NewsArticle(
                        title = title,
                        description = preview,
                        source = source,
                        url = actualUrl
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return articles
}

private fun getDemoNews(category: String): List<NewsArticle> {
    return when(category.lowercase()) {
        "technology" -> listOf(
            NewsArticle("AI Breakthroughs Continue to Transform Industries", "Artificial intelligence is reshaping healthcare, finance, and manufacturing with unprecedented capabilities.", "Tech News", "https://news.google.com"),
            NewsArticle("New Smartphone Technology Promises Longer Battery Life", "Revolutionary battery technology could extend smartphone usage to several days on a single charge.", "Tech Daily", "https://news.google.com"),
            NewsArticle("Cybersecurity Threats on the Rise in 2025", "Security experts warn of increasingly sophisticated cyberattacks targeting businesses and individuals.", "Security News", "https://news.google.com"),
            NewsArticle("Quantum Computing Reaches New Milestone", "Scientists achieve breakthrough in quantum computing, bringing practical applications closer to reality.", "Science Tech", "https://news.google.com"),
            NewsArticle("Electric Vehicle Sales Surge Globally", "EV adoption accelerates worldwide as prices drop and charging infrastructure expands.", "Auto Tech", "https://news.google.com")
        )
        "business" -> listOf(
            NewsArticle("Stock Markets Reach Record Highs", "Major indices hit all-time highs driven by strong corporate earnings and economic optimism.", "Financial Times", "https://news.google.com"),
            NewsArticle("Major Tech Company Announces Acquisition", "Industry leaders make strategic moves to expand market presence and technology capabilities.", "Business Today", "https://news.google.com"),
            NewsArticle("Global Trade Agreements Shape New Economy", "New international partnerships reshape commerce and create opportunities for growth.", "Economic Review", "https://news.google.com"),
            NewsArticle("Startup Funding Hits New Records", "Venture capital investment reaches unprecedented levels as innovation accelerates.", "Venture News", "https://news.google.com"),
            NewsArticle("Consumer Spending Trends Show Growth", "Retail sales data indicates strong consumer confidence and economic recovery.", "Market Watch", "https://news.google.com")
        )
        "science" -> listOf(
            NewsArticle("New Discovery in Cancer Research Shows Promise", "Researchers identify novel treatment approach that shows remarkable results in early trials.", "Science Daily", "https://news.google.com"),
            NewsArticle("Climate Change Study Reveals Concerning Trends", "Latest research highlights urgent need for action to address environmental challenges.", "Environmental Science", "https://news.google.com"),
            NewsArticle("Space Exploration Missions Set for 2025", "Multiple agencies plan ambitious missions to explore Mars, the Moon, and beyond.", "Space News", "https://news.google.com"),
            NewsArticle("Breakthrough in Renewable Energy Technology", "Scientists develop more efficient solar panels that could revolutionize clean energy.", "Green Tech", "https://news.google.com"),
            NewsArticle("Ocean Research Uncovers New Species", "Marine biologists discover fascinating new life forms in deep-sea exploration.", "Marine Biology", "https://news.google.com")
        )
        "health" -> listOf(
            NewsArticle("New Study Links Diet to Mental Health", "Research reveals strong connection between nutrition and psychological well-being.", "Health Today", "https://news.google.com"),
            NewsArticle("Exercise Guidelines Updated for 2025", "Health organizations recommend new approaches to physical activity for optimal benefits.", "Fitness News", "https://news.google.com"),
            NewsArticle("Medical Breakthrough in Heart Disease Treatment", "Innovative therapy shows promise in reducing cardiovascular disease mortality.", "Medical Journal", "https://news.google.com"),
            NewsArticle("Mental Health Awareness Grows Worldwide", "Societies increasingly recognize importance of psychological wellness and support.", "Psychology Today", "https://news.google.com"),
            NewsArticle("Preventive Care Shows Long-Term Benefits", "Studies confirm that regular health screenings significantly improve outcomes.", "Healthcare Weekly", "https://news.google.com")
        )
        "sports" -> listOf(
            NewsArticle("Championship Finals Draw Record Viewership", "Millions tune in to watch thrilling conclusion to the season's biggest competition.", "Sports News", "https://news.google.com"),
            NewsArticle("Olympic Athletes Prepare for Upcoming Games", "Top competitors from around the world train intensively for global sporting event.", "Olympic Update", "https://news.google.com"),
            NewsArticle("Local Team Wins Regional Tournament", "Underdog squad overcomes odds to claim victory in dramatic final match.", "Sports Daily", "https://news.google.com"),
            NewsArticle("New Training Methods Improve Athletic Performance", "Sports scientists develop innovative techniques that enhance player capabilities.", "Sports Science", "https://news.google.com"),
            NewsArticle("Youth Sports Programs See Increased Participation", "Communities invest in athletics programs that benefit young people's development.", "Community Sports", "https://news.google.com")
        )
        else -> listOf(
            NewsArticle("Breaking News: Major Event Unfolds", "Developing story captures global attention as events continue to evolve.", "News Network", "https://news.google.com"),
            NewsArticle("International Summit Addresses Global Issues", "World leaders gather to discuss cooperation on pressing international challenges.", "World News", "https://news.google.com"),
            NewsArticle("Local Community Celebrates Achievement", "Residents come together to mark significant milestone in town's history.", "Local News", "https://news.google.com"),
            NewsArticle("Weather Patterns Show Seasonal Changes", "Meteorologists predict typical conditions as seasons shift across regions.", "Weather Update", "https://news.google.com"),
            NewsArticle("Cultural Festival Attracts Thousands", "Annual celebration of heritage and tradition draws enthusiastic crowds.", "Culture News", "https://news.google.com")
        )
    }
}
