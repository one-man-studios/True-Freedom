package com.weaponized.recovery.data

import androidx.compose.ui.graphics.Color

/**
 * All static content for the app — facts, damage cards, benefits, myths,
 * milestones, sources, T-Maxx data, and brain-state presets.
 *
 * Mirrors the content of the web version (easypeasy.html).
 */

// ─────────────────────────────────────────────────────────────────────────────
// FACTS — THE WARNING carousel
// ─────────────────────────────────────────────────────────────────────────────
data class Fact(
    val n: String,
    val text: String,   // may contain **bold** markers
    val cite: String
)

val FACTS: List<Fact> = listOf(
    Fact("01", "The CIA ran **Operation Midnight Climax** — safe houses with prostitutes, dosing unwitting targets with LSD, filming them through one-way mirrors. **Pornography was the bait.**", "CIA · 1955–1965 · Declassified 1975"),
    Fact("02", "Project **MKUltra** used sexually explicit material to break subjects for interrogation. **149 sub-projects. 44 universities. 12 hospitals.** The state has always known what porn does to the mind.", "CIA MKUltra · Document 06760269"),
    Fact("03", "Against Sukarno, the CIA produced a **pornographic film** to discredit a sitting head of state. Sex as a **weapon of political control.**", "Project Happy Days · Indonesia · 1958"),
    Fact("04", "Porn use **burns out your pleasure sensors** — the same damage seen in cocaine users. You are not addicted to pleasure. **You are addicted to a depleted baseline.**", "Voon et al · Cambridge · 2014"),
    Fact("05", "The **chaser effect**: one session re-opens a 72-hour window of amplified craving. The release you feel is not satisfaction — it is paying an instalment on a debt still on the books.", "Easy Peasy Method · Hackauthor"),
    Fact("06", "Pornography is not a reward. It is a hunger that **creates the hunger** it claims to satisfy. **The first hit gives you nothing but the desire for the next.**", "Easy Peasy Method · Hackauthor"),
    Fact("07", "Willpower-based quitting is the worst kind. It keeps the **Little Monster** alive by treating desire as a real thing worth resisting. The desire is a chemical echo — not a wish.", "Easy Peasy Method · Hackauthor"),
    Fact("08", "Dr. **Frank Olson** died in 1953 after CIA colleagues dosed him with LSD without consent. The program crossed every line — and was **deliberately destroyed in 1973** by Director Helms.", "CIA MKUltra · Church Committee · 1975"),
    Fact("09", "Every view is **logged, profiled, monetised.** You are not the customer. You are the product being pacified — and the data trail is permanent, sold, and resold.", "Surveillance economics · 2024"),
    Fact("10", "The book names the only real barrier to quitting: **FEAR.** Not the porn. The fear of losing it. The fear is the addiction pretending to be you.", "Easy Peasy Method · Hackauthor"),
    Fact("11", "Porn-induced **erectile dysfunction** is now documented in men in their twenties. The brain trained on pixels cannot respond to a real body.", "Park et al · Behavioral Sciences · 2016"),
    Fact("12", "The end state is not **'I am resisting.'** It is **'the question no longer arises.'** That is the natural floor once the chemical debt is paid. **You give up nothing real.**", "Easy Peasy Method · Hackauthor")
)

// ─────────────────────────────────────────────────────────────────────────────
// DAMAGE — THE DAMAGE carousel
// ─────────────────────────────────────────────────────────────────────────────
data class DmgStat(val k: String, val v: String)
data class Damage(
    val cat: String,
    val iconKey: String,
    val amber: Boolean,
    val title: String,
    val body: String,
    val stats: List<DmgStat>
)

val DAMAGE: List<Damage> = listOf(
    Damage("BRAIN", "brain", false,
        "Pleasure sensors **burnt out**",
        "Porn use **destroys your pleasure sensors** by up to 30% — the same damage as cocaine. Each session makes the next one feel less satisfying, requiring more extreme content to feel anything at all. This is not preference drift. It is *brain damage.*",
        listOf(DmgStat("SENSORS LOST", "30%"), DmgStat("RECOVERY", "90 DAYS"))),
    Damage("CHEMISTRY", "chemistry", false,
        "The **chaser debt**",
        "Every session bills you for **72 hours** of amplified craving. The release you feel is not pleasure — it is paying an instalment on a debt still on the books. The bill arrives again on day three. This is why 'just one more' is a lie the chemistry tells.",
        listOf(DmgStat("DEBT WINDOW", "72H"), DmgStat("PEAK", "DAY 3"))),
    Damage("STATE", "state", false,
        "Weaponised since **1953**",
        "The CIA has used sexually explicit material as a weapon for **72 years** — MKUltra, Midnight Climax, Project Happy Days. Whether commercial porn was 'engineered' or merely tolerated, the *function is identical:* pacification, humiliation, leverage.",
        listOf(DmgStat("YEARS", "72"), DmgStat("PROGRAMS", "149"))),
    Damage("SURVEILLANCE", "surveillance", true,
        "You are the **product**",
        "Every view logged. Every preference profiled. Every search monetised and resold to data brokers, advertisers, and — via subpoena — to governments. **There is no private consumption of pornography.** The trail is permanent. The dossier already exists.",
        listOf(DmgStat("LOGGED", "ALL"), DmgStat("DELETABLE", "NEVER"))),
    Damage("BODY", "body", false,
        "Real **bodies stop working**",
        "Porn-induced erectile dysfunction is now documented in men in their twenties. The brain trained on infinite novelty and pixel-perfect angles cannot respond to an ordinary human body. **The dysfunction is the symptom. The damage is in the reward circuit.**",
        listOf(DmgStat("AGE", "20s"), DmgStat("REVERSIBLE", "YES"))),
    Damage("TIME", "time", true,
        "The hours **burned**",
        "Average users lose between **5–15 hours per week** — a full waking day each month, two weeks each year, entire years of a single life consumed. This is time you do not get back. Time stolen from sleep, work, relationships, the actual life you meant to live.",
        listOf(DmgStat("WEEKLY", "10H"), DmgStat("YEARLY", "520H"))),
    Damage("MIND", "mind", false,
        "Self-control **eroded**",
        "Chronic porn use shrinks **the part of your brain responsible for self-control** — impulse control, decision-making, long-term planning. The very part of the brain you need to quit is the part the addiction erodes. This is why 'just stop' fails. The organ that would stop is damaged.",
        listOf(DmgStat("SHRINKAGE", "5%"), DmgStat("RECOVERY", "8 MO"))),
    Damage("PSYCHE", "psyche", false,
        "Memory **rewired**",
        "The memory and emotion centers of your brain show measurable stress after chronic high-speed porn use. Real memories fade. Synthetic ones persist. The user remembers **pixel scenes more vividly than real intimate moments.**",
        listOf(DmgStat("STRESS", "+40%"), DmgStat("MEMORY", "FADED"))),
    Damage("FUTURE", "future", true,
        "Your **children's** baseline",
        "Early exposure (now average age **11**) wires a developing brain around pixel-reward before it ever wires around a real human. The addiction becomes the *template.* Generational baseline shift. You are not just harming yourself — you are normalising the harm for everyone who comes after.",
        listOf(DmgStat("EXPOSURE", "AGE 11"), DmgStat("GEN Z", "AFFECTED")))
)

// ─────────────────────────────────────────────────────────────────────────────
// MILESTONES
// ─────────────────────────────────────────────────────────────────────────────
data class Milestone(val d: Int, val t: String, val x: String)

val MILESTONES: List<Milestone> = listOf(
    Milestone(1, "The first morning", "The chemical debt is loudest now. Most relapses happen in this window."),
    Milestone(3, "Chaser peaks", "72 hours since last session. The bill arrives. Hold the line."),
    Milestone(7, "The fog lifts", "Your brain is starting to heal. Urges arrive as echo, not command."),
    Milestone(14, "The ordinary returns", "Boredom stops being an emergency. Colour returns to ordinary things."),
    Milestone(30, "The question thins", "The thought still appears — but strange, like a childhood belief."),
    Milestone(90, "Baseline returns", "Your brain is approximately healed. The urge is a memory of a memory."),
    Milestone(180, "The door was open", "You stop counting. The cage was never locked.")
)

// ─────────────────────────────────────────────────────────────────────────────
// BENEFITS
// ─────────────────────────────────────────────────────────────────────────────
data class Benefit(val iconKey: String, val title: String, val desc: String)

val BENEFITS: List<Benefit> = listOf(
    Benefit("energy", "Energy returns", "The lethargy lifts within weeks. You stop needing artificial stimulation just to feel awake. Your baseline energy returns to where it was before the addiction drained it."),
    Benefit("focus", "Focus sharpens", "Brain fog clears. You can read, work, and think deeply again without your mind drifting to craving. Attention span rebuilds over 30–90 days."),
    Benefit("testosterone", "Testosterone recovers", "Studies show porn use suppresses T. Quitting — combined with sleep, sun, and lifting — restores natural testosterone production. Libido returns to normal."),
    Benefit("memory", "Memory improves", "The hippocampus recovers. Real memories become vivid again. You stop remembering pixel scenes more clearly than real moments."),
    Benefit("mood", "Mood stabilizes", "The dopamine rollercoaster ends. No more post-session crashes, no more irrational irritability. Emotional baseline becomes steady and calm."),
    Benefit("sleep", "Sleep deepens", "Without the late-night dopamine hits, your circadian rhythm resets. You fall asleep faster, sleep deeper, wake clearer."),
    Benefit("social", "Social anxiety drops", "The shame and secrecy that fuel social anxiety dissolve. Eye contact becomes natural again. Conversation flows without the background noise of guilt."),
    Benefit("attraction", "Real attraction returns", "PIED reverses. Your brain rewires to respond to real human beings, not pixels. Intimacy becomes possible again — physically and emotionally."),
    Benefit("timeReclaimed", "Time reclaimed", "10+ hours per week returned to your life. That's 520 hours a year — a full waking month. Time for your actual life: work, relationships, growth."),
    Benefit("selfRespect", "Self-respect rebuilds", "The daily cycle of promise-and-failure ends. You keep your word to yourself. Trust in yourself returns — and with it, confidence."),
    Benefit("drive", "Drive and ambition", "The flatline motivation lifts. You start wanting things again — and pursuing them. The energy you were burning on pixels redirects to real goals."),
    Benefit("emotional", "Emotional depth", "Feelings return — both good and hard. You can feel joy, sadness, love, and loss without numbing. Life becomes textured instead of grey."),
    Benefit("worldview", "Clearer worldview", "The distortion lifts. You see people as people, not objects. You see situations clearly, not through the lens of a depleted, craving brain."),
    Benefit("freedom", "Freedom", "The cage door was open the whole time. You stop being a passenger to a chemical compulsion. You become the author of your own attention again.")
)

// ─────────────────────────────────────────────────────────────────────────────
// MYTHS — NO BENEFITS view
// ─────────────────────────────────────────────────────────────────────────────
data class Myth(val myth: String, val truth: String)

val MYTHS: List<Myth> = listOf(
    Myth("\"It's natural — humans have always looked at sex.\"",
        "There is nothing natural about high-speed internet pornography. Your brain evolved to respond to a real human in front of you — not to an infinite stream of pixel-perfect novelty. **This is a supernormal stimulus, not a natural one.** The 'natural' argument is the addiction wearing a biology costume."),
    Myth("\"It relieves stress.\"",
        "It creates stress. The relief you feel is the temporary lifting of the craving that the porn itself created. You are not relieving stress — you are paying interest on a debt. **The stress is the addiction. The 'relief' is the addiction pausing.**"),
    Myth("\"It helps me sleep.\"",
        "It destroys sleep. The dopamine spike and subsequent crash dysregulate your circadian rhythm. The 'sleepiness' you feel is exhaustion from a nervous system that just got hammered. **Real sleep comes from a calm brain — not a depleted one.**"),
    Myth("\"It's educational.\"",
        "It is the opposite of education. It distorts bodies, performance, and intimacy into a hyperstimulating fiction that makes real intimacy impossible. **You are being miseducated.** The lessons it teaches are wrong, and they unlearn themselves slowly, painfully, only after you stop."),
    Myth("\"Everyone does it.\"",
        "Millions have quit. Millions more never started. The 'everyone does it' line is a marketing argument, not a truth. **Even if everyone did it, that would not make it beneficial.** Everyone used to smoke. Everyone was wrong."),
    Myth("\"It's just release — a harmless bodily function.\"",
        "It is a chemical debt. The release you feel is not satisfaction — it is the lighter feeling of paying an instalment on a debt still on your books. **Real release does not create a craving for more. Porn always creates a craving for more.** That is the entire mechanism of the trap."),
    Myth("\"It's better than cheating on a partner.\"",
        "This is a false choice invented by the addiction. The real alternative is presence — being fully there with a real person. **Porn does not prevent cheating. It erodes the capacity for real intimacy that makes fidelity natural.** It is not the lesser evil. It is the erosion of the good."),
    Myth("\"I can stop anytime.\"",
        "Then stop. Right now. Forever. If you can, you have lost nothing. If you cannot, you have proven the point. **The phrase 'I can stop anytime' is itself the signature of an addiction that has not yet been interrupted.** Non-addicts do not need to say it."),
    Myth("\"It's not an addiction — I just choose to.\"",
        "Choice does not require justification. If you truly chose it, you would not need to argue for it. The need to defend it is the addiction defending its supply. **A free choice does not produce a craving when removed.** Remove it. See what happens. That is the test."),
    Myth("\"Quitting won't change anything.\"",
        "It changes everything. Energy, focus, mood, attraction, sleep, confidence, time, self-respect — all return. Not as rewards, but as the natural floor once the debt is paid. **The only people who say quitting changes nothing are people who have not quit.**")
)

// ─────────────────────────────────────────────────────────────────────────────
// SOURCES — categorized
// ─────────────────────────────────────────────────────────────────────────────
data class Source(
    val cat: String,
    val type: String,
    val title: String,
    val meta: String,
    val link: String
)

val SOURCES: List<Source> = listOf(
    // METHOD & BOOKS
    Source("METHOD & BOOKS", "BOOK", "The Easy Peasy Method",
        "Hackauthor · Free hackbook based on Allen Carr's EasyWay. The core method this app is built on. Short, unflinching, and free.",
        "https://easypeasymethod.org/"),
    Source("METHOD & BOOKS", "BOOK RESOURCES", "Easy Peasy — Resources Page",
        "Companion resources: REBT coping statements, AVRT technique, meditation guides, community letters.",
        "https://easypeasymethod.org/resources.html"),
    Source("METHOD & BOOKS", "BOOK", "Allen Carr's EasyWay to Stop Smoking",
        "The original method Easy Peasy is adapted from. Same principle: understanding over willpower. Over 30 million copies sold.",
        "https://www.allencarr.com/easyway-treatments/stop-smoking/"),
    Source("METHOD & BOOKS", "BOOK", "Rational Recovery — Jack Trimpey",
        "Source of AVRT (Addictive Voice Recognition Technique). Combined with Easy Peasy in the resources page. The book that teaches you to recognize the 'addictive voice' as separate from your true self.",
        "https://www.rational.org/the-book-rational-recovery/"),

    // DNS PORNOGRAPHY BLOCKERS
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "AdGuard Family Protection DNS",
        "Blocks adult content + malware at the network level. IPv4: 94.140.14.15 / 94.140.15.16 · IPv6: 2a10:50c0::bad1:ff / 2a10:50c0::bad2:ff · Works on router, phone, or computer. No account required.",
        "https://adguard-dns.io/en/public-dns.html"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "CleanBrowsing Family Filter",
        "Strict adult-content filter at DNS level. IPv4: 185.228.168.168 / 185.228.169.168 · IPv6: 2a0d:2a00:1:: / 2a0d:2a00:2:: · Free, with optional paid tiers for customization. Strongest free filtering available.",
        "https://cleanbrowsing.org/family-filter/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "OpenDNS FamilyShield",
        "Cisco's free family-friendly DNS. Blocks adult content automatically. IPv4: 208.67.222.123 / 208.67.220.123 · No account required, no configuration beyond DNS change.",
        "https://www.opendns.com/home-internet-security/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "Cloudflare 1.1.1.3 for Families",
        "Cloudflare's malware + adult-content blocking DNS. IPv4: 1.1.1.3 / 1.0.0.3 · IPv6: 2606:4700:4700::1113 / 2606:4700:4700::1003 · Fast, free, privacy-respecting. Blocks malware on 1.1.1.2 variant.",
        "https://one.one.one.one/family/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "NextDNS",
        "Highly configurable DNS with adult content filter, custom blocklists, and analytics. Free tier covers 300K queries/month. Configure blocklists including the EasyList Adult + OISD filters for maximum coverage.",
        "https://nextdns.io/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "DNS for Family",
        "Free DNS that blocks adult content and pornography. IPv4: 94.130.180.225 / 94.130.180.226 · IPv6: 2a01:4f8:1c0c:503b::225 / 2a01:4f8:1c0c:503b::226 · Operated by volunteers, no logging.",
        "https://dnsforfamily.com/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "DNS BLOCKER", "Comodo Secure DNS — Family",
        "Comodo's family protection variant. IPv4: 8.26.56.26 / 8.20.247.20 · Blocks adult, malware, and phishing. Free, no account.",
        "https://comodo.com/secure-dns/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "BLOCKLIST", "OISD Adult Blocklist",
        "The most widely-used, regularly updated adult-content blocklist. Add it to your NextDNS, Pi-hole, AdGuard Home, or pfBlockerNG setup. Free and open.",
        "https://oisd.nl/downloads"),
    Source("DNS PORNOGRAPHY BLOCKERS", "BLOCKLIST", "EasyList Adult Filter",
        "Subscription filter list for uBlock Origin, AdGuard, Pi-hole, and similar. Blocks adult advertising and content. Add it on top of your main ad blocker.",
        "https://easylist.to/easylist/easylist_adult.txt"),
    Source("DNS PORNOGRAPHY BLOCKERS", "SELF-HOSTED", "Pi-hole — Network-wide Ad & Porn Blocker",
        "Self-hosted DNS sinkhole that runs on a Raspberry Pi or any Linux box. Blocks ads, trackers, and porn at the network level for every device on your Wi-Fi. Pair with OISD/EasyList Adult for full coverage.",
        "https://pi-hole.net/"),
    Source("DNS PORNOGRAPHY BLOCKERS", "SELF-HOSTED", "AdGuard Home",
        "Self-hosted network-level ad and tracker blocker. Acts as your DNS server and applies filter lists to all devices. Easier setup than Pi-hole and supports DNS-over-HTTPS/TLS out of the box.",
        "https://github.com/AdguardTeam/AdGuardHome"),

    // BLOCKING APPS & SOFTWARE
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "BlockerX",
        "Android/iOS app that blocks porn across browsers and apps. Uses AI to detect adult content in real-time, even on unblocked sites. Includes accountability partner feature and keyword blocking.",
        "https://blockerx.org/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Cold Turkey Blocker",
        "Desktop blocker for Windows/Mac. Hard to bypass — even safe mode won't disable it. Block sites, apps, and the entire internet during scheduled focus blocks. Free tier + paid Micromanager.",
        "https://getcoldturkey.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Freedom",
        "Cross-platform blocker (Windows, Mac, iOS, Android, Chrome). Syncs block sessions across all your devices at once — so you can't just switch devices to bypass. Premium subscription.",
        "https://freedom.to/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Covenant Eyes",
        "Internet accountability and filtering software. Screenshots are reviewed by AI for explicit content and shared with your accountability partner. The original accountability app. Paid subscription.",
        "https://www.covenanteyes.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Accountable2You",
        "Accountability software that monitors all your devices and sends reports to your accountability partner. Works on iOS, Android, Windows, Mac, Kindle. Less intrusive than Covenant Eyes, cheaper.",
        "https://www.accountable2you.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Fortify",
        "Free recovery app specifically designed for porn addiction. Daily check-ins, community, training videos, and streak tracking. Built by the Brainbuddy team. Free for teens and students.",
        "https://www.joinfortify.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Brainbuddy",
        "Mobile app that uses CBT techniques to help quit porn. Daily mood tracking, urge management, relapse prevention. Freemium model. Recommended for habit-tracking alongside this web app.",
        "https://www.brainbuddyapp.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Truple",
        "Accountability app that takes random screenshots on your devices and sends them to your accountability partner. Hardest to bypass because it can't be predicted. Android, Windows, Chromebook.",
        "https://www.truple.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "BuluBox",
        "Sandboxed browser for Android that blocks adult content by default. No VPN needed, works locally. Good for when you still need a browser on your phone but want a hard floor.",
        "https://bulubox.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Net Nanny",
        "Long-established parental-control and filtering software. Now owned by Zift. Strong porn filtering across desktop and mobile. Paid subscription.",
        "https://www.netnanny.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Qustodio",
        "Cross-platform parental control with strong adult-content filtering, screen-time limits, and activity reports. Works on Windows, Mac, iOS, Android, Kindle, Chromebook.",
        "https://www.qustodio.com/"),
    Source("BLOCKING APPS & SOFTWARE", "APP BLOCKER", "Bark",
        "Monitors texts, emails, and 30+ social networks for adult content, sexual content, and other risks. Sends alerts to a parent/accountability partner rather than blocking outright.",
        "https://www.bark.us/"),

    // BROWSER EXTENSIONS
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "uBlock Origin",
        "The standard open-source ad/content blocker for Chrome, Firefox, Edge. Add the EasyList Adult filter list in Settings → Filter lists → Adult content. Free, no telemetry.",
        "https://github.com/gorhill/uBlock"),
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "BlockSite",
        "Chrome/Firefox/Edge extension. Block sites by keyword or URL, set focus mode, redirect to motivational pages. Premium syncs across devices. Has accountability feature.",
        "https://blocksite.co/"),
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "StayFocusd",
        "Chrome extension. Set time limits on time-wasting sites, or block them entirely during work hours. Free. The 'Nuclear Option' makes blocks unbypassable for a set period.",
        "https://chrome.google.com/webstore/detail/stayfocusd/laankejkbhbdhmipfmgcngdelahlfolo"),
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "FoxFilter",
        "Firefox extension. Filters adult content based on keywords and URL patterns. Free with premium features for password protection and custom block pages.",
        "https://addons.mozilla.org/en-US/firefox/addon/foxfilter/"),
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "uBlacklist",
        "Cross-browser extension (Chrome/Firefox/Safari) to block specific sites from appearing in search results. Effective against finding new porn sites via Google/Bing/DuckDuckGo. Supports subscription lists.",
        "https://github.com/iorate/uBlacklist"),
    Source("BROWSER EXTENSIONS", "BROWSER EXT", "Simple Blocker",
        "Lightweight Chrome extension. Block any site by URL. Minimal UI, no tracking. Good for users who want simplicity over features. Free and open source.",
        "https://chrome.google.com/webstore/detail/simple-blocker/cgigfpjaefiecohfhahdmkbcbkgjefcj"),
    Source("BROWSER EXTENSIONS", "BROWSER", "Brave Browser",
        "Built-in Shields block ads, trackers, and adult-content ads by default. Private tabs use Tor for anonymity. Set DNS to AdGuard Family in Settings → Shields → Advanced Controls for porn filtering.",
        "https://brave.com/"),

    // COMMUNITIES
    Source("COMMUNITIES", "COMMUNITY", "r/NoFap",
        "Reddit community with 1M+ members. Daily check-ins, success stories, peer support. The original NoFap movement hub. Also runs the annual NoNutNovember challenge.",
        "https://www.reddit.com/r/NoFap/"),
    Source("COMMUNITIES", "COMMUNITY", "r/pornfree",
        "Reddit community focused specifically on quitting porn (separate from NoFap — focused on porn use specifically rather than masturbation). 500K+ members. Stronger recovery orientation.",
        "https://www.reddit.com/r/pornfree/"),
    Source("COMMUNITIES", "COMMUNITY", "r/NoSurf",
        "Reddit community for quitting compulsive internet use. Often overlaps with porn recovery — addresses the underlying attention-addiction mechanism.",
        "https://www.reddit.com/r/nosurf/"),
    Source("COMMUNITIES", "COMMUNITY", "NoFap Forum",
        "Independent forum (separate from Reddit). Older community with reboot logs, accountability threads, and religion-specific subforums. Less noise than the subreddit.",
        "https://forum.nofap.com/"),
    Source("COMMUNITIES", "COMMUNITY", "Reboot Nation",
        "Community founded by porn-induced ED recoverer Gabe Deem. Forum, YouTube channel, recovery resources. Focused on the neurological recovery side, especially PIED.",
        "https://www.rebootnation.org/"),
    Source("COMMUNITIES", "COMMUNITY", "Fight The New Drug",
        "Non-profit organization raising awareness about porn's harms. Educational content, live presentations, community. Founded 2009. Non-religious, science-based.",
        "https://fightthenewdrug.org/"),
    Source("COMMUNITIES", "COMMUNITY", "NoFap Discord",
        "Real-time accountability and chat. Voice channels for urge support, daily check-ins, partner matching. Faster response than forums when an urge hits.",
        "https://discord.gg/nofap"),

    // EDUCATIONAL
    Source("EDUCATIONAL", "EDUCATIONAL", "Your Brain On Porn — Research",
        "Comprehensive collection of peer-reviewed studies, case studies, and articles on internet pornography's neurological effects. Maintained by Gary Wilson. Hundreds of citations.",
        "https://yourbrainonporn.com/research/"),
    Source("EDUCATIONAL", "EDUCATIONAL", "Fight The New Drug — Resources",
        "Educational non-profit providing articles, videos, and live presentations on pornography's effects on individuals, relationships, and society. Free downloadables.",
        "https://fightthenewdrug.org/resources/"),
    Source("EDUCATIONAL", "EDUCATIONAL", "Reboot Nation — PIED Recovery",
        "Gabe Deem's resources specifically on porn-induced erectile dysfunction and the recovery timeline. Critical reading for anyone experiencing PIED.",
        "https://www.rebootnation.org/porn-induced-ed/"),

    // DECLASSIFIED & STUDIES
    Source("DECLASSIFIED & STUDIES", "DECLASSIFIED", "CIA MKUltra — Document 06760269",
        "Project MKUltra: 149 sub-projects, 44 universities, 12 hospitals. LSD dosing, Operation Midnight Climax brothels, behavioral manipulation. Files destroyed in 1973 by Director Helms.",
        "https://archive.org/details/cia-readingroom-document-06760269"),
    Source("DECLASSIFIED & STUDIES", "DECLASSIFIED", "MKULTRA Briefing Book — 149 Subprojects",
        "Full text of the MKULTRA briefing book containing summaries of all 149 subprojects. Declassified and hosted on archive.org.",
        "https://archive.org/stream/DOC_0000190090/DOC_0000190090_djvu.txt"),
    Source("DECLASSIFIED & STUDIES", "GOVERNMENT", "Senate MKUltra Hearing — 1977 (PDF)",
        "US Senate Select Committee on Intelligence hearing transcript on MKUltra. The official government record of the investigation.",
        "https://www.intelligence.senate.gov/wp-content/uploads/2024/08/sites-default-files-hearings-95mkultra.pdf"),
    Source("DECLASSIFIED & STUDIES", "GOVERNMENT", "Church Committee Report — 1975-76",
        "US Senate investigation that brought MKUltra to public light. Documented CIA abuse of unwitting subjects, including Frank Olson who died after being dosed with LSD.",
        "https://www.intelligence.senate.gov/site/1975-76churchcommittee.html"),
    Source("DECLASSIFIED & STUDIES", "STUDY", "Park et al — Is Internet Pornography Causing Sexual Dysfunctions? (2016)",
        "Peer-reviewed study in Behavioral Sciences (PMC5039517). Reviews PIED and clinical reports of erectile dysfunction in men in their 20s caused by porn use. Reversible with abstinence.",
        "https://pmc.ncbi.nlm.nih.gov/articles/PMC5039517/"),
    Source("DECLASSIFIED & STUDIES", "STUDY", "Banca & Voon et al — Compulsivity in Sexual Behaviors (2016)",
        "Frontiers in Behavioral Neuroscience. Cambridge research showing novelty-seeking and compulsivity patterns in compulsive sexual behavior users. Demonstrates the D2 dopamine receptor downregulation mechanism.",
        "https://www.frontiersin.org/journals/behavioral-neuroscience/articles/10.3389/fnbeh.2016.00154/full"),
    Source("DECLASSIFIED & STUDIES", "STUDY", "Novelty, Conditioning and Attentional Bias to Sexual Rewards — Voon",
        "Scilit publication. Voon and colleagues on how pornography conditions the brain toward novelty and away from real rewards. Core neuroscience behind the 'chaser effect'.",
        "https://www.scilit.com/publications/9f03a1923544efa3f7a278da3e12c6c9"),
    Source("DECLASSIFIED & STUDIES", "RESEARCH", "Lewis et al — D2 Receptors in Cocaine Addiction (2020)",
        "PMC7971775. Establishes that dopamine D2 receptor downregulation is the common neurological lesion across all chemical addictions — the same mechanism triggered by compulsive sexual behavior.",
        "https://pmc.ncbi.nlm.nih.gov/articles/PMC7971775/"),
    Source("DECLASSIFIED & STUDIES", "RESEARCH", "Volkow et al — Dopamine D2 Receptor and Obesity",
        "The Lancet. Nora Volkow's landmark research showing that D2 receptor downregulation occurs in all reward-pathway addictions — drugs, food, and compulsive behaviors. Foundational to the addiction neuroscience field.",
        "https://www.thelancet.com/article/S0140-6736(00)04986-2/fulltext"),
    Source("DECLASSIFIED & STUDIES", "HISTORICAL", "Operation Midnight Climax — MKULTRA Subproject 39",
        "CIA declassified document. Subproject 39 of MKUltra — the safe houses in San Francisco where prostitutes dosed unwitting targets with LSD while agents filmed through one-way mirrors. Pure blackmail operation.",
        "https://archive.org/download/DOC_0000017436/DOC_0000017436.pdf")
)

// ─────────────────────────────────────────────────────────────────────────────
// T-MAXX — habits, diet, exercise
// ─────────────────────────────────────────────────────────────────────────────
data class Habit(val id: String, val name: String)
data class TCard(val iconKey: String, val title: String, val why: String, val desc: String)

val TMAXX_HABITS: List<Habit> = listOf(
    Habit("sleep", "7–9 hours sleep"),
    Habit("sun", "Morning sunlight (10+ min)"),
    Habit("cold", "Cold shower"),
    Habit("nofap", "No porn / no fap"),
    Habit("lift", "Heavy compound lift"),
    Habit("protein", "Adequate protein (1g/lb)"),
    Habit("zinc", "Zinc-rich food"),
    Habit("vitd", "Vitamin D / sun"),
    Habit("meditate", "Stress reduction / meditation"),
    Habit("noalcohol", "No alcohol"),
    Habit("hydrate", "3+ liters water"),
    Habit("nosugar", "No processed sugar")
)

val TMAXX_DIET: List<TCard> = listOf(
    TCard("eggs", "Eggs", "CHOLESTEROL + PROTEIN", "Cholesterol is the raw material for testosterone. Egg yolks are packed with it, plus vitamin D and choline. **Eat 3–4 daily.**"),
    TCard("redMeat", "Red Meat", "ZINC + SATURATED FAT", "Grass-fed beef delivers zinc, iron, and saturated fat — all essential for T production. **2–3 times per week.**"),
    TCard("oysters", "Oysters", "ZINC BOMB", "The single highest zinc source. Zinc deficiency directly lowers T. **6–12 oysters weekly, or zinc supplement.**"),
    TCard("nuts", "Brazil Nuts", "SELENIUM", "Just 2–3 per day gives your full selenium requirement. Selenium supports T and thyroid function. **Don't overdo it — selenium toxicity is real.**"),
    TCard("avocado", "Avocados", "MONOUNSATURATED FAT", "Healthy fats are non-negotiable for hormone production. Avocados deliver monounsaturated fat plus boron. **1 daily.**"),
    TCard("oil", "Extra Virgin Olive Oil", "POLYPHENOLS + FAT", "Anti-inflammatory fats that support hormone synthesis. **Use as your primary cooking oil.**"),
    TCard("vegetables", "Cruciferous Vegetables", "DIM — ESTROGEN CONTROL", "Broccoli, cauliflower, cabbage. DIM helps metabolize and clear excess estrogen. **Eat daily.**"),
    TCard("pomegranate", "Pomegranate", "NITRIC OXIDE + T", "Studies show pomegranate juice raises T and improves blood flow. **Drink daily or eat the seeds.**"),
    TCard("garlic", "Garlic", "CORTISOL REDUCTION", "Garlic lowers cortisol — the stress hormone that directly competes with testosterone production. **1–2 cloves daily.**"),
    TCard("onions", "Onions", "QUERCETIN + SULFUR", "Animal studies show onions boost T levels significantly. Quercetin reduces cortisol. **Eat liberally.**")
)

val TMAXX_EXERCISE: List<TCard> = listOf(
    TCard("squats", "Squats", "KING OF T-BOOSTERS", "The single most effective exercise for testosterone. Heavy, full-body, systemic stress. **3–4 sets of 5 reps, heavy.**"),
    TCard("deadlift", "Deadlifts", "FULL-BODY COMPOUND", "Recruits more muscle than any other lift. Massive hormonal response. **1–2 times per week, heavy.**"),
    TCard("bench", "Bench Press", "UPPER BODY COMPOUND", "Heavy pressing movements trigger T release. Pair with rows for balance. **3–4 sets of 5–8 reps.**"),
    TCard("ohp", "Overhead Press", "SHOULDER + CORE", "Standing heavy pressing builds full-body tension and triggers hormonal response. **3–4 sets of 5 reps.**"),
    TCard("pullups", "Pull-ups / Rows", "POSTERIOR CHAIN", "Heavy pulling balances pressing and builds the back. **3–4 sets to near failure.**"),
    TCard("hiit", "HIIT Sprints", "T + GROWTH HORMONE", "Short, explosive sprint intervals (30s sprint / 90s walk × 6–8) spike T and GH. **1–2 times per week.**"),
    TCard("avoid", "AVOID: Chronic Cardio", "CORTISOL SPIKE", "Long steady-state cardio (1+ hour runs) raises cortisol and LOWERS testosterone. **Keep cardio short and intense, or skip it.**"),
    TCard("frequency", "Frequency: 3–4x/week", "OPTIMAL RECOVERY", "Lift heavy 3–4 days per week. Rest days are when T is produced. Overtraining lowers T. **Sleep and rest are part of the program.**")
)

// ─────────────────────────────────────────────────────────────────────────────
// BRAIN STATES — presets for the brain canvas
// ─────────────────────────────────────────────────────────────────────────────
data class BrainState(
    val pleasure: Int,
    val control: Int,
    val craving: Int,
    val title: String,
    val sub: String,
    val badge: String,
    val badgeColor: Color,
    val desc: String,
    val cite: String,
    val color: Color,
    val accent: Color,
    val chaos: Float,
    val dimRatio: Float,
    val pulseSpeed: Float,
    val fireChance: Float
)

val BRAIN_RELAPSE = BrainState(
    pleasure = 30, control = 40, craving = 95,
    title = "After Relapse",
    sub = "Your brain's pleasure sensors are burnt out. Every signal fires at once but nothing gets through clearly. This is why nothing feels good — and why the urge feels like a command.",
    badge = "RELAPSE",
    badgeColor = Color(0xFFFF1A1A),
    desc = "Your pleasure sensors are burnt out. Only 30% remain. Self-control is weakened to 40%. Craving is at 95% — screaming. Every signal fires at once but nothing gets through clearly. This is why nothing feels good anymore, and why the urge feels like a command you can't refuse. **This is the chaser, made visible.**",
    cite = "— Based on Cambridge brain scan studies (Voon et al, 2014) and the Easy Peasy Method",
    color = Color(0xFFE03030),
    accent = Color(0xFFFF6B6B),
    chaos = 0.7f,
    dimRatio = 0.55f,
    pulseSpeed = 1.4f,
    fireChance = 0.06f
)

val BRAIN_RECOVERY = BrainState(
    pleasure = 100, control = 100, craving = 5,
    title = "After Recovery",
    sub = "Your brain has healed. Pleasure sensors are full. Signals flow calmly. The urge is gone — not because you're fighting it, but because the root that grew it has withered.",
    badge = "HEALED",
    badgeColor = Color(0xFF22F08A),
    desc = "Your brain has fully healed. Pleasure sensors at 100%. Self-control at 100%. Craving at 5% — barely a whisper. Signals flow calmly and in harmony. The urge is gone — not because you're fighting it, but because the chemical root that grew it has withered away. **The question doesn't even arise.**",
    cite = "— Based on recovery studies and the Easy Peasy Method · Day 90+ baseline",
    color = Color(0xFF3ED898),
    accent = Color(0xFFAAFFCC),
    chaos = 0.06f,
    dimRatio = 0.0f,
    pulseSpeed = 0.7f,
    fireChance = 0.015f
)

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

/** Convert **bold** markers to AnnotatedString-friendly segments. */
data class StyledSegment(val text: String, val bold: Boolean)

fun parseBold(input: String): List<StyledSegment> {
    val out = mutableListOf<StyledSegment>()
    val regex = Regex("\\*\\*(.+?)\\*\\*")
    var lastIdx = 0
    for (m in regex.findAll(input)) {
        if (m.range.first > lastIdx) {
            out.add(StyledSegment(input.substring(lastIdx, m.range.first), false))
        }
        out.add(StyledSegment(m.groupValues[1], true))
        lastIdx = m.range.last + 1
    }
    if (lastIdx < input.length) {
        out.add(StyledSegment(input.substring(lastIdx), false))
    }
    return out
}
