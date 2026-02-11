<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# OkHttpParamsGet Changelog

## [Unreleased]

## [2.2.2] - 2026-02-11

### Changed

- Support K2 model
- Update `platformVersion` to  `2025.2.5`
- Change since build to `242` (2024.2)
- Upgrade `JDK` to `21`

## [2.2.1] - 2023-05-28

### Fixed

- Fixed crash bug

## [2.2.0] - 2023-05-04

### Changed

- Update `platformVersion` to  `2023.1.1`
- Change since build to `231` (2023.1)
- Upgrade `JDK` to `17`

### [2.1.3] - 2023-05-03

### Changed

- Exclude `static` field

### Fixed

- Fixed bugs

## [2.1.2] - 2020-08-31

### Changed
- Support implements interface

### Fixed
- Fix use `open fun` in final class

## [2.1.1] - 2020-04-30

### Changed
- Change since-build to `182`

## [2.1.0] - 2020-04-29

### Changed
- Support OkHttp4.x

## [2.0.1] - 2019-12-17

### Fixed
- Fix not auto import when fields is empty

## [2.0.0] - 2019-12-13

### Changed
- Replaced `@PostFiles` by `@PostFile`. OkHttpParamsGet can identify the type of request, supporting list, array and map
- Add `@ParamName`. Support custom param name
- Now `@PostFile` support `java.io.File`. And support custom key
- Settings added the switch between `HashMap` and `ArrayMap`

## [1.5.3] - 2019-10-23

### Changed
- Set default annotation when the value is non-nullable

## [1.5.2] - 2019-06-24

### Changed
- Replace `HashMap` with `ArrayMap`

## [1.5.1] - 2019-03-30

### Fixed
- Fix array to string bug. Now use `Arrays.toString()`

## [1.5.0] - 2019-03-02

### Changed
- Support Kotlin
- Support null check. When the value is null, you can either add or add as an empty string
- Support `Map<String, Object>`
- Allow customizing the class that uploads the file. Use annotations on the property or method to identify key, filename, mime-type and data
- Added annotations:
    - `@Key`: post file key
    - `@Filename`: post file filename
    - `@MimeType`: post file mime-type
    - `@Data`: post file data

## [1.4.0] - 2017-08-17

### Changed
- Auto import class

## [1.3.0] - 2017-06-15

### Changed
- Update the params name
- Add get body (returns `MultipartBody.Builder`). You can build a `RequestBody` and set upload progress listener

## [1.2.0] - 2017-03-09

### Fixed
- Update Id
- Fix keyboard shortcut cannot use
- Fix annotations useless bug
- Fix an `AnActionEvent` bug (IntelliJ API doesn't allow sharing `AnActionEvent` between swing events)

## [1.1.0] - 2017-03-08

### Changed
- Optimized the code display

## [1.0.0] - 2017-03-08

### Added
- Annotations:
    - `@Ignore`: don't add to params
    - `@PostFile`: post a file
    - `@PostFiles`: post files
- `FileInput`: upload File class (see project `extra/FileInput.java`)
