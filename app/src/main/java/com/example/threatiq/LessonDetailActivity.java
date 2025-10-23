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

        // Views for the default lesson layout
        NestedScrollView defaultScrollView = findViewById(R.id.default_content_scroll_view);
        TextView defaultContentTextView = findViewById(R.id.lesson_detail_content);

        // Views for the special Phishing lesson layout
        RelativeLayout phishingContainer = findViewById(R.id.phishing_content_container);
        ViewPager2 viewPager = findViewById(R.id.phishing_view_pager);
        // ImageView arrowPrevious = findViewById(R.id.arrow_previous); // This line is not needed
        // ImageView arrowNext = findViewById(R.id.arrow_next); // This line is not needed
        ImageView arrowPrevious = findViewById(R.id.arrow_previous);
        ImageView arrowNext = findViewById(R.id.arrow_next);


        // --- Set Common Data ---
        collapsingToolbar.setTitle(lessonTitle);
        lessonDetailImage.setImageResource(lessonImageResId);

        // --- Content Loading Logic ---
        if ("Phishing".equals(lessonTitle)) {
            // This is the Phishing lesson, so set up the special swipeable view.
            defaultScrollView.setVisibility(View.GONE); // Hide the default view
            phishingContainer.setVisibility(View.VISIBLE); // Show the phishing container

            // Create the pages and set up the adapter
            List<SpannableStringBuilder> pages = createPhishingPages();
            PhishingPagerAdapter adapter = new PhishingPagerAdapter(this, pages);
            viewPager.setAdapter(adapter);

            // --- THE TABLAYOUTMEDIATOR CODE HAS BEEN REMOVED ---

            // --- ARROW CLICK LOGIC ---
            arrowPrevious.setOnClickListener(v -> {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1, true); // true for smooth scroll
                }
            });

            arrowNext.setOnClickListener(v -> {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < adapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1, true); // true for smooth scroll
                }
            });

            // This callback shows/hides the arrows when the page changes.
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // Hide "Previous" arrow on the first page, show otherwise
                    arrowPrevious.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                    // Hide "Next" arrow on the last page, show otherwise
                    arrowNext.setVisibility(position == adapter.getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
                }
            });

            // Manually set the initial visibility for the first load.
            arrowPrevious.setVisibility(View.INVISIBLE);
            if (adapter.getItemCount() > 1) {
                arrowNext.setVisibility(View.VISIBLE);
            } else {
                arrowNext.setVisibility(View.INVISIBLE);
            }

        } else {
            // This is any other lesson, so use the simple default TextView.
            phishingContainer.setVisibility(View.GONE); // Hide the phishing container
            defaultScrollView.setVisibility(View.VISIBLE); // Show the default view

            // Use an if-else if chain for content
            String content;
            if ("Password Security".equals(lessonTitle)) {
                content = "Strong passwords are your first line of defense. A strong password should be:\n\n" +
                        "- At least 12 characters long.\n" +
                        "- A mix of uppercase letters, lowercase letters, numbers, and symbols.\n" +
                        "- Not easily guessable (avoid names, birthdays, or common words).";
            }
            // Add more lessons here with "else if"
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

        // Page 1: Main Description
        String page1Title = "What is Phishing?";
        String page1Content = "Phishing is an attack that attempts to steal your money, or your identity, by getting you to reveal personal information such as credit card numbers, bank information, or passwords on websites that pretend to be legitimate. Cybercriminals typically pretend to be reputable companies, friends, or acquaintances in a fake message, which contains a link to a phishing website.";
        pages.add(buildPage(page1Title, page1Content));

        // Page 2: Spear Phishing
        String page2Title = "Spear Phishing";
        String page2Content = "Think of spear phishing as professional phishing. Classic phishing campaigns send mass emails to as many people as possible, but spear phishing is much more targeted. The hacker has either a certain individual(s) or organization they want to compromise and are after more valuable info than credit card data. They do research on the target to make the attack more personalized and increase their chances of success.";
        pages.add(buildPage(page2Title, page2Content));

        // Page 3: Email/Spam
        String page3Title = "Email/Spam";
        String page3Content = "Using the most common phishing technique, the same email is sent to millions of users with a request to fill in personal details. These details will be used by the phishers for their illegal activities. Most of the messages have an urgent note which requires the user to enter credentials to update account information, change details, or verify accounts. Sometimes, they may be asked to fill out a form to access a new service through a link which is provided in the email.";
        pages.add(buildPage(page3Title, page3Content));

        // Page 4: Ransomware
        String page4Title = "Ransomware";
        String page4Content = "Ransomware denies access to a device or files until a ransom has been paid. Ransomware for PC's is malware that gets installed on a user’s workstation using a social engineering attack where the user gets tricked in clicking on a link, opening an attachment, or clicking on malvertising.";
        pages.add(buildPage(page4Title, page4Content));

        //Page 5:Website Forgery
        String page5Title = "Website Forgery";
        String page5Content = "Forged websites are built by hackers made to look exactly like legitimate websites. The goal of website forgery is to get users to enter information that could be used to defraud or launch further attacks against the victim.";
        pages.add(buildPage(page5Title, page5Content));

        // Page 6: Indicators of a Phishing Attack
        String page6Title = "Indicators of a Phishing Attack\n";
        String page6Content = "\tMismatch between emails and urls\n" +
                                "\tUnusual greetings\n" +
                                "\tUnusual attachments\n" +
                                "\tLow quality graphics\n" +
                                "\tRequest for sensitive information\n" +
                                "\tTypos and errors\n";
        pages.add(buildPage(page6Title, page6Content));

        // Page 7: Why is understanding the risk of Phishing important?
        String page7Title = "Why is understanding the risk of Phishing important?\n";
        String page7Content = "Phishing attacks are a constant threat to campus and are becoming increasingly sophisticated. Successful Phishing attacks can:\n\n" +
                "\tCause financial loss for victims\n" +
                "\tPut their personal information at risk\n" +
                "\tPut university data and systems at risk\n";
        pages.add(buildPage(page7Title, page7Content));

        return pages;
    }

    private List<SpannableStringBuilder> malwarePages() {
        List<SpannableStringBuilder> pages = new ArrayList<>();

        // Page 1: Main Description
        String page1Title = "What is Phishing?";
        String page1Content = "Phishing is an attack that attempts to steal your money, or your identity, by getting you to reveal personal information such as credit card numbers, bank information, or passwords on websites that pretend to be legitimate. Cybercriminals typically pretend to be reputable companies, friends, or acquaintances in a fake message, which contains a link to a phishing website.";
        pages.add(buildPage(page1Title, page1Content));

        // Page 2: Spear Phishing
        String page2Title = "Spear Phishing";
        String page2Content = "Think of spear phishing as professional phishing. Classic phishing campaigns send mass emails to as many people as possible, but spear phishing is much more targeted. The hacker has either a certain individual(s) or organization they want to compromise and are after more valuable info than credit card data. They do research on the target to make the attack more personalized and increase their chances of success.";
        pages.add(buildPage(page2Title, page2Content));

        // Page 3: Email/Spam
        String page3Title = "Email/Spam";
        String page3Content = "Using the most common phishing technique, the same email is sent to millions of users with a request to fill in personal details. These details will be used by the phishers for their illegal activities. Most of the messages have an urgent note which requires the user to enter credentials to update account information, change details, or verify accounts. Sometimes, they may be asked to fill out a form to access a new service through a link which is provided in the email.";
        pages.add(buildPage(page3Title, page3Content));

        // Page 4: Ransomware
        String page4Title = "Ransomware";
        String page4Content = "Ransomware denies access to a device or files until a ransom has been paid. Ransomware for PC's is malware that gets installed on a user’s workstation using a social engineering attack where the user gets tricked in clicking on a link, opening an attachment, or clicking on malvertising.";
        pages.add(buildPage(page4Title, page4Content));

        //Page 5:Website Forgery
        String page5Title = "Website Forgery";
        String page5Content = "Forged websites are built by hackers made to look exactly like legitimate websites. The goal of website forgery is to get users to enter information that could be used to defraud or launch further attacks against the victim.";
        pages.add(buildPage(page5Title, page5Content));

        // Page 6: Indicators of a Phishing Attack
        String page6Title = "Indicators of a Phishing Attack\n";
        String page6Content = "\tMismatch between emails and urls\n" +
                "\tUnusual greetings\n" +
                "\tUnusual attachments\n" +
                "\tLow quality graphics\n" +
                "\tRequest for sensitive information\n" +
                "\tTypos and errors\n";
        pages.add(buildPage(page6Title, page6Content));

        // Page 7: Why is understanding the risk of Phishing important?
        String page7Title = "Why is understanding the risk of Phishing important?\n";
        String page7Content = "Phishing attacks are a constant threat to campus and are becoming increasingly sophisticated. Successful Phishing attacks can:\n\n" +
                "\tCause financial loss for victims\n" +
                "\tPut their personal information at risk\n" +
                "\tPut university data and systems at risk\n";
        pages.add(buildPage(page7Title, page7Content));

        return pages;
    }

    /**
     * Helper method to build a styled page with a title and content.
     */
    private SpannableStringBuilder buildPage(String title, String content) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // Add title and style it
        ssb.append(title);
        int end = ssb.length();
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new RelativeSizeSpan(1.3f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Add content if it exists
        if (content != null && !content.isEmpty()) {
            ssb.append("\n\n"); // Add space after title
            ssb.append(content);
        }
        return ssb;
    }

    // Handle the back button press in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // Closes the current activity and goes back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
