package com.kaushalya.karnataka.presentation.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    LegalScaffold(title = "Privacy Policy", onBack = onBack, body = PRIVACY_BODY)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    LegalScaffold(title = "Terms of Service", onBack = onBack, body = TERMS_BODY)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalScaffold(title: String, onBack: () -> Unit, body: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())
        ) {
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private const val PRIVACY_BODY = """Last updated: April 2026

Kaushalya Karnataka ("we", "the app") is operated by the Kaushalya Karnataka team. This policy explains what we collect, how we use it, and your rights.

1. Information we collect
- Phone number (for sign-in via OTP)
- Display name and role (Worker / Customer)
- For workers: bio, town, locality, categories, services, photos, ratings, reviews you have received
- For customers: bookmarks, reviews you have left, hire requests you have sent
- Crash and analytics events (Firebase Crashlytics + Analytics)
- Device language preference

2. How we use it
- To run the app: show profiles, match customers with workers, store reviews
- To improve the app: aggregated, anonymous analytics
- To diagnose crashes and prevent abuse

3. Sharing
- Worker profiles are public to all signed-in customers
- We do NOT sell your data to advertisers
- We share data only with Google Firebase (our backend) under their terms

4. Your rights (DPDP Act 2023)
- Access, correct, or erase your data: use the Delete account option in Profile, or email privacy@kaushalya-karnataka.example
- Withdraw consent at any time by signing out and uninstalling

5. Retention
- Active accounts: kept while you use the app
- Deleted accounts: removed within 30 days
- Crash logs: retained 90 days

6. Contact
privacy@kaushalya-karnataka.example
"""

private const val TERMS_BODY = """Last updated: April 2026

By using Kaushalya Karnataka, you agree to these terms.

1. Eligibility
You must be 18+ and legally able to enter contracts in India.

2. Worker conduct
- Listings must be accurate. Misleading prices, fake portfolio images, or impersonation are prohibited.
- You are responsible for delivering the service you list.
- Disputes between worker and customer are between the parties; the app is a discovery platform, not an escrow service.

3. Customer conduct
- Reviews must be honest and based on actual experience.
- Defamatory, abusive, or false reviews will be removed and may result in account suspension.

4. Content moderation
We may remove content or suspend accounts that violate these terms or applicable law.

5. Liability
The app is provided as-is. We are not liable for the quality of services performed, payment disputes, or damages arising from interactions arranged through the platform.

6. Changes
We may update these terms. Material changes will be highlighted in-app.

7. Governing law
Karnataka, India. Disputes go to Bengaluru courts.
"""
