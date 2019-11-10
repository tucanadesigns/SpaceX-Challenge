# SpaceX-Challenge
Coding Challenge

## Architecture

The architecture of this application follows a single-activity, MVVM approach, using fragments to provide the view layer, and view models to maintain the data state. The view models are android lifecycle-aware, employing mutable live data components to expose observables to the view layer.

The network requests, and response deserialisation to Kotlin data classes, is facilitated through a repository singleton class, with call backs to listener interfaces on the view models.

The main activity implements a fragment listener interface to coordinate data passing between the fragments and access to services provided by the activity. The mission details fragment is constructed through a static factory method to allow the persisting of passed arguments across lifecycle and configuration changes.

---

## Assumptions

A goal of the challenge was to avoid the use of third-party libraries. This has been fully achieved, but at the expense of a robust network layer, employing very little in the way of response error handling, network connectivity management, and response timeouts. A more mature solution would benefit from the use of a solid, industry-standard network library such as Retrofit2 (preferred) or Volley.

The UI for this challenge has been left as very basic in terms of colour, animation, large-screen support, etc. A master-detail layout for larger screens would be an appropriate consideration for a production-grade application.
