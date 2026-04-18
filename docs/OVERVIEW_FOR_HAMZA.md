# EuropeBikeFinder — overview for Hamza

This note explains what the product is, what you can do with it, and what to expect—**without technical jargon**.

---

## What this is

**EuropeBikeFinder** is an internal tool for **PatrickBike** to see **e-bike offers from Europe** in one place, with prices thought through in **Swiss francs (CHF)**—including a rough idea of import and delivery—so you can compare opportunities against the **Swiss market** and plan buying or sourcing.

It is **not** a public shop. It is a **workspace** for your team.

---

## What you use in the browser

You open the app like a normal website. You can switch **language** between **English** and **German (Switzerland)**.

Typical areas you will see:

- **Listings** — recent offers from the data that has been collected into the system.
- **Wish search** — search using the same kind of criteria you use for a “wish bike” (type of bike, condition, motor, budget, country, and so on).
- **Other sections** — for example tools around **competitive pricing**, **competitor watch**, and a **sourcing directory** (where to look for stock), depending on what is turned on for you.

If the team has switched on a **login**, you will see a **sign-in page** first. After you enter the password the team gives you, you can use the app until you sign out or your session ends.

You can **sort** listings (for example by date or price) and **narrow by country** when that is available—so you can focus on offers from places you care about.

---

## Where the information comes from

The numbers are built from **real listing data** that the system **collects and stores** over time (for example from partner shops and marketplaces), plus **reference information** used to estimate Swiss market levels and “good deal” signals.

**Important:** landed CHF figures and savings are **estimates**. Always check **current customs, VAT, and seller conditions** before you buy.

---

## The optional “AI brief” (competitor watch)

In the **competitor** area there can be a feature that **writes a short text summary** for you—based on **automated snapshots** of competitor sites and, when configured, a bit of **recent market context**.

- You only use paid **AI services when someone actually asks for that summary**—not all day in the background.
- If this is **not** set up, the rest of the app still works; you simply won’t get that generated text.

So: **running the app day to day does not mean “paying for AI nonstop.”** AI cost is mainly tied to **how often that summary is requested**.

---

## What “running” the app means in simple terms

Think of three pieces:

1. **The website you click** — what you see in the browser.
2. **The engine behind it** — where calculations, searches, and scheduled checks run.
3. **The filing cabinet** — where listing data and settings are stored safely.

Those three are usually hosted in **different places** in production: the website on a fast web host, the engine and the data on servers the team chooses. That is normal for this kind of product.

**Costs** are mostly: **hosting** those pieces, **the database**, and **internet traffic**. The **AI text** is an **extra**, usage-based cost only if you use that feature.

---

## Privacy and responsibility

The app can hold **business-sensitive** information. Passwords and access should be treated like any internal tool. Legal pages (privacy, imprint) exist in the app; final wording for Switzerland may still need a legal review before a public or wide launch.

---

## If something is wrong

If the page does not load, or login fails, or numbers look empty, that is usually **configuration, data not imported yet, or a temporary outage**—your tech contact can check. You do not need to know how it is built to report **what you tried** and **what you saw**.

---

## One sentence summary

**EuropeBikeFinder helps PatrickBike see and filter European e-bike opportunities in CHF, compare them to the Swiss context, and watch competitors—with optional AI text only when you ask for it.**
