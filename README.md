# Examer 📖 (Work-in-progress 🚧)
[![Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release suitable for the public.](https://www.repostatus.org/badges/latest/wip.svg)](https://www.repostatus.org/#wip)![Android CI workflow](https://github.com/t3chkid/Examer/actions/workflows/Android-CI.yaml/badge.svg)

Examer is a **work-in-progress** Android app that is used to take tests that evaluate a person's ability to listen to, and understand conversations in English.
To try out this app you need to use the latest version of [Android Studio Arctic Fox](https://developer.android.com/studio?gclid=EAIaIQobChMInc7OlbDD9QIVmpJmAh2lKgaZEAAYASAAEgLvsfD_BwE&gclsrc=aw.ds).

## Screenshots
![akt](screenshots/screenshot-compilation.png)

## Tech stack 
- Entirely written in [Kotlin](https://kotlinlang.org/).
- Manual dependency injection.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the UI.
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) for
  threading.
- [Timber](https://github.com/JakeWharton/timber) for logging.
- [Firebase Authentication](https://firebase.google.com/docs/auth) for user account creation and authentication.
- [Firebase Cloud Firestore](https://firebase.google.com/products/firestore?gclid=EAIaIQobChMIqcK24rDD9QIVCJhmAh12WAxqEAAYASAAEgLMnPD_BwE&gclsrc=aw.ds) for storing data.
- [Firebase Storage](https://firebase.google.com/products/storage) for storing audio files and images.
- [Coil (Compose)](https://coil-kt.github.io/coil/compose/) for image loading and caching.
- [Accompanist library](https://google.github.io/accompanist/) for window insets and pager,pager-indicators,swiperefresh,placeholder.

## Source code and architecture
- [Architecture components](https://developer.android.com/topic/libraries/architecture/) such as
  Lifecycle and ViewModels are used.
- [MVVM](https://developer.android.com/jetpack/guide?gclid=EAIaIQobChMI-_GIsejG8QIVzNaWCh0NXQANEAAYASAAEgKZ2fD_BwE&gclsrc=aw.ds)
  architecture is used.
- Source code conforms to the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Dependency injection is done manually.
- [Material design color system](https://material.io/design/color/the-color-system.html#color-usage-and-palettes)
  specification is used for assigning colors to the UI components.
- Commit messages follow
  the [Angular specification](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines)
  for commit messages.
- [Github actions](https://github.com/features/actions) for continuous integration (CI).
