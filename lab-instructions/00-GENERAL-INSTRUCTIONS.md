# Lab Instructions — General (HT26)

**Course:** Principles & Practices in Software Testing · **DV033G** · Mid Sweden University
**Examination component:** **L101 — Labs · 4 credits · Pass/Fail**

System under test: **WalkMates** — people matched with animal-care opportunities. See the
repo [`README.md`](../README.md) and [`docs/REQUIREMENTS.md`](../docs/REQUIREMENTS.md).

> **Version:** HT26 v1.0, frozen 2026-08-26. Lab scope, deliverables, and assessment bars are
> fixed for this course run; see the [freeze policy](../LAB_MATERIAL_VERSION.md). Minor errata
> and clarifications may still be published without changing the required work.

---

## 1. The four labs

Four labs, all **mandatory**, all done **in pairs**.

| Lab | Title | Modules | Where |
|---|---|---|---|
| **Lab 1** | Fundamentals & Specification-based testing | M1 + M2 | 🗂️ Repo (WalkMates) |
| **Lab 2** | Structural testing & Test optimization | M3 + M4 | 🗂️ Repo (WalkMates) |
| **Lab 3** | Research trends & Testing AI | M5 | 🗂️ Repo (WalkMates) |
| **Lab 4** | Ethics & Sustainability | M6 | 📄 Canvas (paper-based) |

> **Labs 1–3 are done in the WalkMates repository.** **Lab 4 is the exception** — a paper-based
> analysis submitted via Canvas.

### Repository choice and teacher access

For Labs 1–3, your pair may work in a public fork of WalkMates or clone it locally and push
to your own private GitHub repository, whichever is easier. Private repositories must invite
the teaching team so they can access the submitted commit and CI evidence. Include the
repository link and a specific commit/tag in your Canvas submission.

Reflections and written analyses do not need to be public or stored in Git. Word and other
writing tools are welcome. Use the required reflection questions and include all written
artifacts requested by the lab, then export to PDF for Canvas. Markdown files and suggested
repository paths are optional; no duplicate Markdown copy is required.

The labs are designed as one journey up the **test-level ladder**: run the system → write your
first test → specification-based unit tests (Lab 1) → structural coverage and component
isolation (Lab 2) → testing an AI feature through its interface (Lab 3) → step back and reason
about ethics and sustainability (Lab 4).

## 2. Released all at once

**All four labs are released in week 36.** No drip-feed, no gating — work ahead if you like.
Deadlines pace the cohort:

| Lab | Released | Due |
|---|---|---|
| Lab 1 | Wk 36 | Wk 38 |
| Lab 2 | Wk 36 | Wk 40 |
| Lab 3 | Wk 36 | Wk 41–42 |
| Lab 4 | Wk 36 | Wk 42 |

**Three attempts** per lab: the in-schedule deadline → week 1 of the next study period → exam
week of study period 2.

## 3. How labs are assessed — *machines grade mechanics, humans grade understanding*

Each lab submission has **two parts**:

1. **Repo work, checked by CI (automatic).** When you push, the **GitHub Actions** pipeline runs
   the build and the tooling and posts a **summary check** with build status, **coverage %**,
   and (where relevant) **mutation score**. *You do not submit screenshots — the CI result is
   the evidence.* (Lab 4 has no repo/CI part.)
2. **A short reflection (graded by the instructor).** One reflection per lab, using the fixed
   [`reflection-template.md`](reflection-template.md) questions. Write in a separate document
   or optionally copy the template to `reflections/labN-reflection.md`. Submit the reflection
   in your Canvas PDF. **This is the artifact graded for understanding.** Keep it short and specific —
   judgment, not length.

A lab **passes** when the CI evidence meets the lab's stated bar **and** the reflection shows
genuine understanding.

> **Using AI is encouraged.** The reflection is where you show your judgment over what AI
> produced — what it suggested, what you kept or changed, and why.

## 4. What you submit (per lab)

- **Labs 1–3:** upload a PDF containing your completed reflection, the lab's other required
  written artifacts, and a link to your pair's repository at a specific commit/tag. Ensure
  the teaching team has access if it is private.
- **Lab 4:** upload a PDF containing the paper-based analysis and completed reflection to Canvas.

No long synthesized report is required.

## 5. What you need (Labs 1–3)

- A **GitHub account** with access to your pair's repository. The GitHub Student Developer Pack
  is useful but is not required for these labs.
- **Java 21 + Maven** locally — see [`FIRST_TEST_TUTORIAL.md`](../FIRST_TEST_TUTORIAL.md).
- Tools across labs: **JUnit 5, Mockito, AssertJ, JaCoCo, PIT, GitHub Actions**.

## 6. Generative AI policy

Allowed and encouraged, with transparency. Track your AI use per lab (what for, did it help);
cite AI-generated text; in the reflection, show **what AI suggested vs. what you kept/changed and
why**. Honesty about AI use does not lower your grade.

## 7. Getting started

1. Choose a public fork or your own private repository, then clone your pair's working repo.
2. `mvn clean test` — confirm `BUILD SUCCESS` (the project ships green).
3. Do the [`FIRST_TEST_TUTORIAL.md`](../FIRST_TEST_TUTORIAL.md) (10 min).
4. Open [`01-LAB1-FUNDAMENTALS.md`](01-LAB1-FUNDAMENTALS.md).
