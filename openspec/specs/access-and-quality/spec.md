# access-and-quality Specification

## Purpose

Guest access and baseline quality of hirion.ch (test groups AUTH and QA): guests are sent to sign in
before private pages, the sign-in, password-reset and 404 pages work, and the public site is usable on
a phone, is free of console errors and has basic SEO metadata; step 1 of the signup wizard checks the
input in the browser before it lets the visitor continue.

## Requirements

### Requirement: AUTH-1 Guests are redirected to sign in
A guest with no session who opens a private page `/dashboard` or `/settings` SHALL be redirected to
`/login`, which shows the heading level 1 "Welcome back".

#### Scenario: Guest opens the dashboard
- **WHEN** a guest with no stored session opens `/dashboard`
- **THEN** the browser URL path is `/login` and the page shows the heading level 1 "Welcome back"

#### Scenario: Guest opens settings
- **WHEN** a guest with no stored session opens `/settings`
- **THEN** the browser URL path is `/login` and the page shows the heading level 1 "Welcome back"

### Requirement: AUTH-2 Login page
The page `/login` SHALL show the heading level 1 "Welcome back", the fields labelled exactly "Email" and
"Password", the button "Sign in", the link "Forgot password?" to `/forgot-password` and the link
"Create one" to `/signup`.

#### Scenario: Login page is rendered
- **WHEN** a guest opens `/login`
- **THEN** the page shows the heading level 1 "Welcome back", the fields "Email" and "Password" and the button "Sign in"

#### Scenario: Forgot password link
- **WHEN** a guest opens `/login` and clicks the link "Forgot password?"
- **THEN** the browser URL path is `/forgot-password`

#### Scenario: Create account link
- **WHEN** a guest opens `/login`
- **THEN** the link "Create one" has `href="/signup"`

### Requirement: AUTH-3 Login fields support autofill
On `/login` the field "Email" SHALL have `autocomplete="email"` and the field "Password" SHALL have
`autocomplete="current-password"`, so password managers can fill them. (Known defect D5.)

#### Scenario: Login autocomplete attributes
- **WHEN** a guest opens `/login`
- **THEN** the field "Email" has `autocomplete="email"` and the field "Password" has `autocomplete="current-password"`

### Requirement: AUTH-4 Forgot-password page
The page `/forgot-password` SHALL show the heading level 1 "Reset your password", the field labelled
"Email", the button "Send reset link" and, in its main content, a link "Sign in" to `/login`.

#### Scenario: Forgot-password page is rendered
- **WHEN** a guest opens `/forgot-password`
- **THEN** the page shows the heading level 1 "Reset your password", the field "Email", the button
  "Send reset link" and, in the main content, a link "Sign in" with `href="/login"`

### Requirement: AUTH-5 Unknown pages show a 404 page
Opening a path that does not exist SHALL answer with HTTP status 404 and show the heading level 1 "404",
the text "Page not found" and a link "Go home" to `/`.

#### Scenario: Unknown path
- **WHEN** a guest opens `/this-page-does-not-exist-qa`
- **THEN** the response status is 404, the page shows the heading level 1 "404" and the text
  "Page not found", and the link "Go home" has `href="/"`

### Requirement: QA-1 Home page fits a phone screen
At a viewport of 375 x 812 px the home page `/` SHALL NOT scroll horizontally.

#### Scenario: No horizontal scroll at 375 px
- **WHEN** a guest opens `/` in a 375 x 812 px viewport
- **THEN** `document.documentElement.scrollWidth` is at most 375

### Requirement: QA-3 Public pages load without console errors
The public pages `/`, `/contact`, `/login`, `/privacy`, `/terms` and `/imprint` SHALL load without any
console message of type `error` and without an uncaught page error.

#### Scenario: No console errors on public pages
- **WHEN** a guest opens each of `/`, `/contact`, `/login`, `/privacy`, `/terms` and `/imprint`
- **THEN** no console message of type `error` and no uncaught page error is recorded

### Requirement: QA-4 Home page has SEO basics
The home page `/` SHALL have the document title "Hirion, Swiss jobs matched to your CV, sent by email",
a meta description "Stop scrolling job boards. Hirion scans every Swiss source and delivers only the
roles that match your profile.", a canonical link `https://hirion.ch/`, exactly one heading level 1,
and the site SHALL serve `/robots.txt` and `/sitemap.xml` with HTTP status 200; `/robots.txt` SHALL
disallow the private areas `/dashboard` and `/settings`.

#### Scenario: Home page metadata
- **WHEN** a guest opens `/`
- **THEN** the document title is "Hirion, Swiss jobs matched to your CV, sent by email", the
  `meta[name="description"]` content is "Stop scrolling job boards. Hirion scans every Swiss source and
  delivers only the roles that match your profile.", the `link[rel="canonical"]` href is
  `https://hirion.ch/` and there is exactly one heading level 1

#### Scenario: robots.txt and sitemap.xml
- **WHEN** a client requests `/robots.txt` and `/sitemap.xml`
- **THEN** both responses have HTTP status 200 and `/robots.txt` contains the lines
  "Disallow: /dashboard" and "Disallow: /settings"

### Requirement: REG-1 Signup step 1 validates before continuing
On `/signup`, clicking "Continue" while the first and last name are empty SHALL NOT advance the wizard:
the page SHALL show the notification "Please enter your first and last name." and stay on "Step 1 of 4".

#### Scenario: Empty step 1 is blocked
- **WHEN** a guest opens `/signup` and clicks "Continue" without filling any field
- **THEN** the page shows the text "Please enter your first and last name.", still shows "Step 1 of 4"
  and the URL path stays `/signup`

### Requirement: REG-2 Show password reveals the typed password
On `/signup` the button "Show password" SHALL switch the field "Password" from `type="password"` to
`type="text"`, so the visitor can check what they typed.

#### Scenario: Show password
- **WHEN** a guest opens `/signup`, types a password into the field labelled exactly "Password" and clicks
  the button "Show password"
- **THEN** the field "Password" has `type="text"`
