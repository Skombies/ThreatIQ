package com.example.threatiq;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.util.ArrayList;
import java.util.List;

public class LessonDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // --- Basic Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // --- Get Data from Intent ---
        Intent intent = getIntent();
        String lessonTitle = intent.getStringExtra("LESSON_TITLE");
        int lessonImageResId = intent.getIntExtra("LESSON_IMAGE_RES_ID", 0);

        // --- Find All Views from XML ---
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView lessonDetailImage = findViewById(R.id.lesson_detail_image);
        NestedScrollView defaultScrollView = findViewById(R.id.default_content_scroll_view);
        TextView defaultContentTextView = findViewById(R.id.lesson_detail_content);
        RelativeLayout phishingContainer = findViewById(R.id.phishing_content_container);
        ViewPager2 viewPager = findViewById(R.id.phishing_view_pager);
        ImageView arrowPrevious = findViewById(R.id.arrow_previous);
        ImageView arrowNext = findViewById(R.id.arrow_next);

        // --- Set Common Data ---
        collapsingToolbar.setTitle(lessonTitle);
        lessonDetailImage.setImageResource(lessonImageResId);

        // --- Content Loading Logic ---
        if ("Phishing".equals(lessonTitle) || "Malware".equals(lessonTitle)) {
            // This is a swipeable lesson, so set up the special swipeable view.
            defaultScrollView.setVisibility(View.GONE);
            phishingContainer.setVisibility(View.VISIBLE);

            // Determine which pages to load
            List<SpannableStringBuilder> pages;
            if ("Phishing".equals(lessonTitle)) {
                pages = createPhishingPages();
            } else { // It must be the Malware lesson
                pages = createMalwarePages();
            }

            PhishingPagerAdapter adapter = new PhishingPagerAdapter(this, pages);
            viewPager.setAdapter(adapter);

            // --- ARROW CLICK LOGIC ---
            arrowPrevious.setOnClickListener(v -> {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1, true);
                }
            });

            arrowNext.setOnClickListener(v -> {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < adapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1, true);
                }
            });

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    arrowPrevious.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                    arrowNext.setVisibility(position == adapter.getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
                }
            });

            arrowPrevious.setVisibility(View.INVISIBLE);
            if (adapter.getItemCount() > 1) {
                arrowNext.setVisibility(View.VISIBLE);
            } else {
                arrowNext.setVisibility(View.INVISIBLE);
            }

        } else {
            // This is any other lesson, so use the simple default TextView.
            phishingContainer.setVisibility(View.GONE);
            defaultScrollView.setVisibility(View.VISIBLE);

            String content;
            if ("Password Security".equals(lessonTitle)) {
                content = "Strong passwords are your first line of defense. A strong password should be:\n\n" +
                        "- At least 12 characters long.\n" +
                        "- A mix of uppercase letters, lowercase letters, numbers, and symbols.\n" +
                        "- Not easily guessable (avoid names, birthdays, or common words).";
            }
            // Add more simple lessons here with "else if"
            else {
                // This is the fallback for any lesson that doesn't have custom content yet.
                content = "Content for " + lessonTitle + " goes here.";
            }
            defaultContentTextView.setText(content);
        }
    }

    /**
     * This method builds the list of pages specifically for the Phishing lesson.
     */
    private List<SpannableStringBuilder> createPhishingPages() {
        List<SpannableStringBuilder> pages = new ArrayList<>();
        String page1Title = "What is Phishing?";
        String page1Content = "Phishing is an attack that attempts to steal your money, or your identity, by getting you to reveal personal information such as credit card numbers, bank information, or passwords on websites that pretend to be legitimate. Cybercriminals typically pretend to be reputable companies, friends, or acquaintances in a fake message, which contains a link to a phishing website.";
        pages.add(buildPage(page1Title, page1Content));
        String page2Title = "Spear Phishing";
        String page2Content = "Think of spear phishing as professional phishing. Classic phishing campaigns send mass emails to as many people as possible, but spear phishing is much more targeted. The hacker has either a certain individual(s) or organization they want to compromise and are after more valuable info than credit card data. They do research on the target to make the attack more personalized and increase their chances of success.";
        pages.add(buildPage(page2Title, page2Content));
        String page3Title = "Email/Spam";
        String page3Content = "Using the most common phishing technique, the same email is sent to millions of users with a request to fill in personal details. These details will be used by the phishers for their illegal activities. Most of the messages have an urgent note which requires the user to enter credentials to update account information, change details, or verify accounts. Sometimes, they may be asked to fill out a form to access a new service through a link which is provided in the email.";
        pages.add(buildPage(page3Title, page3Content));
        String page4Title = "Ransomware";
        String page4Content = "Ransomware denies access to a device or files until a ransom has been paid. Ransomware for PC's is malware that gets installed on a user’s workstation using a social engineering attack where the user gets tricked in clicking on a link, opening an attachment, or clicking on malvertising.";
        pages.add(buildPage(page4Title, page4Content));
        String page5Title = "Website Forgery";
        String page5Content = "Forged websites are built by hackers made to look exactly like legitimate websites. The goal of website forgery is to get users to enter information that could be used to defraud or launch further attacks against the victim.";
        pages.add(buildPage(page5Title, page5Content));
        String page6Title = "Indicators of a Phishing Attack\n";
        String page6Content = "\tMismatch between emails and urls\n" +
                "\tUnusual greetings\n" +
                "\tUnusual attachments\n" +
                "\tLow quality graphics\n" +
                "\tRequest for sensitive information\n" +
                "\tTypos and errors\n";
        pages.add(buildPage(page6Title, page6Content));
        String page7Title = "Why is understanding the risk of Phishing important?\n";
        String page7Content = "Phishing attacks are a constant threat to campus and are becoming increasingly sophisticated. Successful Phishing attacks can:\n\n" +
                "\tCause financial loss for victims\n" +
                "\tPut their personal information at risk\n" +
                "\tPut university data and systems at risk\n";
        pages.add(buildPage(page7Title, page7Content));
        return pages;
    }

    /**
     * This method builds the list of pages specifically for the Malware lesson.
     */
    private List<SpannableStringBuilder> createMalwarePages() {
        List<SpannableStringBuilder> pages = new ArrayList<>();

        // Page 1
        String page1Title = "What is Malware?";
        String page1Content = "Malware, short for malicious software, refers to any intrusive software developed by cybercriminals (often called hackers) to steal data and damage or destroy computers and computer systems. Examples of common malware include viruses, worms, Trojan viruses, spyware, adware, and ransomware. Recent malware attacks have exfiltrated data in mass amounts.";
        pages.add(buildPage(page1Title, page1Content));
        // ------------------------------------------------------------------------------------------------------------------------------------------

        // Page 2: Combined Detection Techniques
        String page2Title = "Malware Detection Techniques";

        // First subheading and its content
        String subHeading1 = "Signature-based detection";
        String content1 = "This is a traditional method that involves identifying malware by comparing files to a database of known malware signatures. While signature-based detection is effective for known threats, it struggles with zero-day attacks, new or previously unidentified malware that does not yet have a signature.";

        // Second subheading and its content
        String subHeading2 = "Machine Learning and AI";
        String content2 = "The popularity of machine learning and AI has forced its way into being integrated into malware detection systems by analysing large datasets, identifying patterns, trends and anomalies. A major benefit in machine learning is that it can continuously learn from new data, improving detection capabilities over time and adapt to new threats. However, this adaptation requires extensive skills and training to keep up the advancements.";

        SpannableStringBuilder combinedPage = new SpannableStringBuilder();
        // Add Title
        combinedPage.append(page2Title);
        combinedPage.setSpan(new StyleSpan(Typeface.BOLD), 0, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage.setSpan(new RelativeSizeSpan(1.3f), 0, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Add First Subheading and Content
        combinedPage.append("\n\n");
        int start = combinedPage.length();
        combinedPage.append(subHeading1);
        combinedPage.setSpan(new StyleSpan(Typeface.BOLD), start, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage.setSpan(new RelativeSizeSpan(1.1f), start, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage.append("\n\n").append(content1);

        // Add Second Subheading and Content
        combinedPage.append("\n\n\n"); // Extra space between sections
        start = combinedPage.length();
        combinedPage.append(subHeading2);
        combinedPage.setSpan(new StyleSpan(Typeface.BOLD), start, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage.setSpan(new RelativeSizeSpan(1.1f), start, combinedPage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage.append("\n\n").append(content2);
        pages.add(combinedPage);
        //------------------------------------------------------------------------------------------------------------------------

        // Page 3
        String page3Title = "Malware Detection Techniques";
        String staticAnalysisSubHeading = "Static analysis";
        String staticAnalysisContent = "This technique involves analysing code or binary of a program without actually executing it, allowing the static analysis to identify malicious behaviour by studying the structure, functions and code patterns of a file. Since static analysis doesn’t require running the malware, it can be safely and early on in threat detection without risking activating harmful actions. The limitation of this method is against obfuscated or encrypted code due to potential time constraints.";

        // Content for the second subheading
        String reputationSubHeading = "Reputation-based detection";
        String reputationContent = "This technique is one that evaluates the trustworthiness of files, programs or websites based on their historical behaviour, reputation or association with known threats. Instead of relying on traditional signature- or behaviour-based detection, this approach uses trusted threat intelligence databases and blocks files or URLs based on their reputation scores. Offering low overhead, real-time protection and easily prevents known malicious domains, this technique has a lot to offer. Its weaknesses include being limited to reputation database and relying on external data.";

        // Combining the two onto one page
        SpannableStringBuilder combinedPage2 = buildPage(page3Title, staticAnalysisSubHeading, staticAnalysisContent);

        //
        combinedPage2.append("\n\n\n"); // Extra space between sections
        start = combinedPage2.length();
        combinedPage2.append(reputationSubHeading);
        combinedPage2.setSpan(new StyleSpan(Typeface.BOLD), start, combinedPage2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage2.setSpan(new RelativeSizeSpan(1.1f), start, combinedPage2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage2.append("\n\n").append(reputationContent);
        pages.add(combinedPage2);
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        // Page 4
        String page4Title = "Common types of Malwares";
        String filelessMalwareHeading = "Fileless Malware";
        String filelessMalwareContent = "Fileless malware doesn’t install anything initially, instead, it makes changes to files that are native to the operating system, such as PowerShell or WMI. Because the operating system recognizes the edited files as legitimate, a fileless attack is not caught by antivirus software and because these attacks are stealthy, they are up to ten times more successful than traditional malware attacks.";

        // Content for the second subheading
        String spywareSubHeading = "Spyware";
        String spwareContent = "Spyware collects information about users’ activities without their knowledge or consent. This can include passwords, pins, payment information and unstructured messages.\n" +
                "The use of spyware is not limited to the desktop browser: it can also operate in a critical app or on a mobile phone.\n" +
                "Even if the data stolen is not critical, the effects of spyware often ripple throughout the organization as performance is degraded, and productivity eroded.\n";

        // Combining the two onto one page
        SpannableStringBuilder combinedPage3 = buildPage(page4Title, filelessMalwareHeading, filelessMalwareContent);

        combinedPage3.append("\n\n\n"); // Extra space between sections
        start = combinedPage3.length();
        combinedPage3.append(spywareSubHeading);
        combinedPage3.setSpan(new StyleSpan(Typeface.BOLD), start, combinedPage3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage3.setSpan(new RelativeSizeSpan(1.1f), start, combinedPage3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage3.append("\n\n").append(spwareContent);
        pages.add(combinedPage3);

        // Page 5
        String page5Title = "Common types of Malwares";
        String virusSubHeading = "Virus";
        String virusContent = "A virus is a piece of code that inserts itself into an application and executes when the app is run. Once inside a network, a virus may be used to steal sensitive data, launch DDoS attacks or conduct ransomware attacks.";

        // Content for the second subheading
        String botSubHeading = "Bots/Botnets";
        String botContent = "A bot is a software application that performs automated tasks on command. They’re used for legitimate purposes, such as indexing search engines, but when used for malicious purposes, they take the form of self-propagating malware that can connect back to a central server.";

        // Combining the two onto one page
        SpannableStringBuilder combinedPage4 = buildPage(page5Title, virusSubHeading, virusContent);

        combinedPage4.append("\n\n\n"); // Extra space between sections
        start = combinedPage4.length();
        combinedPage4.append(botSubHeading);
        combinedPage4.setSpan(new StyleSpan(Typeface.BOLD), start, combinedPage4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage4.setSpan(new RelativeSizeSpan(1.1f), start, combinedPage4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        combinedPage4.append("\n\n").append(botContent);
        pages.add(combinedPage4);
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

        return pages;
    }

    /**
     * Helper method to build a styled page with only a title and content.
     */
    private SpannableStringBuilder buildPage(String title, String content) {
        // This is an overloaded method call. It calls the main method with a null subheading.
        return buildPage(title, null, content);
    }

    /**
     * Main helper method to build a styled page with a title, optional subheading, and content.
     */
    private SpannableStringBuilder buildPage(String title, String subHeading, String content) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // Add title and style it (large and bold)
        int start = ssb.length();
        ssb.append(title);
        int end = ssb.length();
        ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new RelativeSizeSpan(1.3f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Add subheading if it exists
        if (subHeading != null && !subHeading.isEmpty()) {
            ssb.append("\n\n"); // Add space after title
            start = ssb.length();
            ssb.append(subHeading);
            end = ssb.length();
            // Style the subheading (medium and bold)
            ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new RelativeSizeSpan(1.1f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Add main content if it exists
        if (content != null && !content.isEmpty()) {
            ssb.append("\n\n"); // Add space after title/subheading
            ssb.append(content);
        }
        return ssb;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
