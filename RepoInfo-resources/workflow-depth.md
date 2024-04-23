## JavaGPT Chat Application Workflow Explanation

The JavaGPT Chat Application is a complex system that integrates user interface actions with backend services, including a chat model and a database. The workflow of this application can be broken down into several key components and their interactions. Below is an in-depth explanation of each step in the workflow, as represented in the UML sequence diagram:

![JavaGPT Chat Application Workflow](./RepoInfo-resources/workflow-diagram-trp.svg)

### 1. Application Launch

- **User to Launcher**: The user starts the application, which triggers the `main` method in the `Launcher` class.

- **Launcher to ChatApp**: The `Launcher` class then calls the `main` method of the `ChatApp` class, which is the entry point to the JavaFX application.

### 2. JavaFX Application Initialization

- **ChatApp to Application**: The `ChatApp` class extends the JavaFX `Application` class. The `start` method is overridden to set up the initial stage (window) of the application.

- **Scene Setup**: The `start` method configures the scene, including layout and styling, using FXML or JavaFX components directly in code.

### 3. Database Connection and Chat History Loading

- **ChatApp to DBUtil**: To load the chat history, `ChatApp` makes a call to `DBUtil.getConnection` to establish a connection with the database.

- **DBUtil to ChatApp**: `DBUtil` returns a `Connection` object to `ChatApp`.

- **Executing SQL Query**: `ChatApp` uses this connection to execute a SQL query that fetches chat history from the database.

- **ResultSet Processing**: The results are processed and displayed in the UI.

### 4. User Interactions

- **Asking Questions**: When the user asks a question, `ChatApp` captures this input and initiates a search by calling the `doSearch` method.

- **Clearing Chat History**: The user can also clear the chat history, which triggers `ChatApp` to call a method that executes a SQL command to delete the history from the database.

### 5. Backend Processing

- **AnswerService Interaction**: For asking questions, `ChatApp` interacts with `AnswerService`, which handles the logic of sending the question to the chat model and receiving the response.

- **Assistant Chat Model**: `AnswerService` uses an `Assistant` (an interface to the chat model) to send the question and receive a stream of tokens (text) as the response.

### 6. Response Handling

- **CustomStreamingResponseHandler**: This handler processes the response tokens, appending them to the current answer and updating the UI accordingly.

- **Completion and Error Handling**: It also handles the completion of the response stream and any errors that might occur during the process.

### 7. Database Updates

- **Saving to Database**: Once a response is complete, the chat message (question and answer) is saved to the database for future retrieval.

- **Clearing Chat History**: Similarly, clearing the chat history involves executing a SQL command to delete records from the chat messages table.

### 8. UI Updates

- **Displaying Messages**: The UI is dynamically updated to display new messages as they are received and processed.

- **Clearing History**: When the chat history is cleared, the UI is also updated to reflect this change, removing all past messages from view.

This workflow encapsulates the lifecycle of a user's interaction with the JavaGPT Chat Application, from launching the application, asking questions, and receiving responses, to managing chat history in both the UI and the database.
