# PulsePrison Addon Example

An example add-on for **PulsePrison** demonstrating how to use its Developer API.
This guide will serve as a foundation for creating your own extensions, custom enchantments, currencies, and integrations.

## Features included in this example

1. **Obtaining the API**: Safely using `PulsePrisonProvider.get()` inside `onEnable`.
2. **Events (Listeners)**: Listening to and modifying or cancelling PulsePrison's custom events, such as `PlayerRebirthEvent` or `MineResetEvent`.
3. **Custom Actions**: Registering a custom action tag (e.g., `[heal]`) that can be used in any GUI menu or leveling reward in the core plugin.
4. **Custom Enchantments**: Creating a Java-based enchantment (`BaseEnchant`) that hooks directly into the mining pipeline, utilizing per-level probability rates.

## How to build this project

1. Clone this repository.
2. Create a folder named `libs` in the root directory.
3. Place your **PulsePrison (v2.0+)** `.jar` file inside the `libs` folder.
4. Run `./gradlew build` to compile the project.
5. The generated `.jar` file will be located in `build/libs/`.

## Documentation

You can find the complete API documentation in the `DOCUMENTATION/DEVELOPER_API.md` file within the core plugin's files.
