
### ![about](https://github.com/joseserize0222/AI-Assistant-Plugin/blob/main/src/main/resources/META-INF/pluginIcon.svg) AI-Assistant IntelliJ IDEA Plugin



The **AI-Assistant Plugin** enhances your development workflow by providing real-time Kotlin code statistics and AI-powered insights. It not only helps analyze your Kotlin files but also allows you to send functions directly to OpenAI's GPT model for a detailed explanation of their purpose.

---

## Key Features

### 1. **Kotlin File Statistics**
- Automatically analyzes Kotlin files and displays:
  - **Total Lines**: The number of lines in the file.
  - **TODO Comments**: Tracks the number of TODO comments.
  - **Longest Function**: Identifies the longest function and provides its content and name.
- **Real-time Updates**: The statistics update as you make changes to the file or switch between files.

![Real-time updates](./src/data/Editing%20the%20file%20audio%20removed.gif)
![Switch files](./src/data/Switching%20files%20and%20resizing%20the%20windows.gif)

### 2. **AI-Powered Function Description**
- **Explain Method Action**: Select a Kotlin function in the editor and click the action button to get an AI-generated description of its purpose using OpenAI's GPT model.
- **Real-time Feedback**: View the response directly within a tool window inside the IDE.

---

## Installation

1. **Install Git** if it is not installed on your system.
2. **Clone the repository** using SSH:  
   `git clone git@github.com:joseserize0222/AI-Assistant-Plugin.git`
3. **Build the plugin**:
  - In the project directory, run:  
    `./gradlew buildPlugin`
4. **Run the plugin**:
  - Execute:  
    `./gradlew runIde`

### Installation via Latest Release:
1. Go to the **Releases** section of the repository.
2. Download the latest `.zip` file.
3. In IntelliJ IDEA, go to **Settings > Plugins > Install Plugin from Disk** and select the downloaded file.

### Setting up OpenAI API Key:

To use the **AI-Powered Function Description** feature, you'll need an API key from OpenAI. Follow these steps:

1. **Create an OpenAI Platform account**:
  - Visit the [OpenAI API Keys Page](https://platform.openai.com/account/api-keys) and generate a new API key.

2. **Set the `OPENAI_API_KEY` environment variable** on your system:
   Depending on your operating system, here’s how to set the environment variable for the API key:

   #### macOS (zsh):
  1. Open your terminal.
  2. Run:  
     `echo 'export OPENAI_API_KEY="your-api-key"' >> ~/.zshrc`
  3. Apply the changes:  
     `source ~/.zshrc`
  - For permanent setup instructions, refer to this [Apple StackExchange answer](https://apple.stackexchange.com/questions/395457/how-to-set-environment-variable-permanently-in-zsh-on-macos-catalina).

   #### Linux (Ubuntu):
  1. Open your terminal.
  2. Run:  
     `echo 'export OPENAI_API_KEY="your-api-key"' >> ~/.bashrc`
  3. Apply the changes:  
     `source ~/.bashrc`
  - For more detailed instructions, check this [Ask Ubuntu thread](https://askubuntu.com/questions/887442/how-to-permanently-set-an-environment-variable).

   #### Windows (CMD):
  1. Open `Command Prompt`.
  2. Run:  
     `setx OPENAI_API_KEY "your-api-key"`
  - For persistent variable setup, follow this [StackOverflow guide](https://stackoverflow.com/questions/5898131/set-a-persistent-environment-variable-from-cmd-exe).

Alternatively, you can set the API key directly in the code:
- In the `ExplainMethodAction.kt` file, modify the `token` initialization:
   ```kotlin
   val token: Token = Token("INSERT YOUR API KEY HERE")
   ```

---

## Plugin Architecture

### Services

1. **FileAnalyzerService**
  - Handles Kotlin file analysis to calculate file statistics like total lines, TODO comments, and the longest function.
  - **Key Methods**:
    - `calculateStats(file: VirtualFile)`: Analyzes the selected file and returns its statistics.
    - `updateStats()`: Updates and displays the latest file statistics.

2. **KtorClientService**
  - Communicates with the OpenAI API, sending selected functions and receiving function descriptions.
  - **Key Methods**:
    - `postFunctionToOpenAi(functionBody: String, token: Token)`: Sends the selected Kotlin function to OpenAI for a description and retrieves the response.

### Tool Windows

1. **ChatGptPanel**
  - Displays AI-generated function descriptions from OpenAI within a tool window.
  - **Key Methods**:
    - `callback(response: String)`: Updates the panel with the AI-generated response.

2. **ProjectStatsPanel**
  - Displays Kotlin file statistics, such as total lines, TODO lines, and the longest function, in real-time.
  - **Key Methods**:
    - `callback(allStats: KotlinFileStats)`: Updates the panel with the latest file statistics.

### Actions

1. **ExplainMethodAction**
  - Allows users to send a selected Kotlin function to OpenAI for explanation.
  - **Key Methods**:
    - `actionPerformed(event: AnActionEvent)`: Sends the selected function's code to the AI and retrieves the explanation.
    - `getActionUpdateThread()`: Specifies the background thread to be used for action updates.

### Utils

- **KotlinFileStats**: Stores the results of file analysis.
  - **Attributes**:
    - `totalLines`: Total lines in the file.
    - `todoLines`: Count of TODO comments.
    - `function`: The longest function in the file.
    - `fileName`: The name of the analyzed file.

- **OpenAIRequest, OpenAIResponse**: Structures for the OpenAI API request and response.

- **Token**: Represents the OpenAI API key.

- **getToken()**: Fetches the API key from the environment.

---

With **AI-Assistant**, Kotlin developers can not only track their code’s complexity and structure but also receive immediate feedback on functions' purposes directly from the OpenAI GPT model. This tool is a must-have for anyone looking to enhance code understanding, refactoring, and overall code management.
