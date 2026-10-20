let tasks = [];

const API_URL = "/api/tasks";

const taskList = document.querySelector(".task-list");
const taskDetails = document.querySelector(".task-details");
const addButton = document .querySelector(".add-task");
const filter = document.querySelector(".filter");
const searchInput = document.querySelector(".search input");

let asideState = { type: null, id: null };

const isMobile = () => window.matchMedia("(max-width: 800px)").matches;

function findTaskElement(id) {
    return document.querySelector(`.task[data-id="${id}"]`);
}

function closeTaskPanel(taskElement) {
    if (!taskElement) return;
    const panel = taskElement.querySelector(":scope > .task-panel");
    if (panel) panel.remove();
    delete taskElement.dataset.panel;
    taskElement.classList.remove("task--selected");
}

function openTaskPanel(taskElement, html, kind) {
    closeTaskPanel(taskElement);
    const panel = document.createElement("div");
    panel.className = "task-panel";
    panel.innerHTML = html;
    taskElement.appendChild(panel);
    taskElement.dataset.panel = kind;
    taskElement.classList.add("task--selected");
    return panel;
}

async function loadTasks(){
    const response = await fetch(API_URL);
    tasks = await response.json();
    displayTasks()}

function displayTask(task){
    const taskElement = document.createElement("article");
    
    taskElement.classList.add("task");
    taskElement.dataset.id = task.id;

    taskElement.innerHTML = `
        <div class="task-main">

            <h2>${task.name}</h2>

                <div>
                    <span>${task.deadline || "___"}</span>
                    <input type="checkbox"
                        class="task-statut"
                        ${task.statut === "Finished" ? "checked" : ""}
>
                </div>
        </div>

        <div class="task-actions">

              <span>Priority: ${task.priority}</span>

              <button class="show-details">👁</button>
              <button class="edit-task">✏️</button>
              <button class="delete-task">🗑️</button>

        </div>
    `;
    const statusCheckbox = taskElement.querySelector(".task-statut");

    statusCheckbox.addEventListener("change", async () => {
        const endpoint = statusCheckbox.checked ? "complete" : "uncomplete";
        await fetch(`${API_URL}/${task.id}/${endpoint}`, {method: "PATCH"});
        await loadTasks();
    });
    const detailsButton = taskElement.querySelector(".show-details");

    detailsButton.addEventListener("click", () => {
        showTaskDetails(task);
    });

    const editButton = taskElement.querySelector(".edit-task");
    editButton.addEventListener("click", () => {
    editTask(task);
    });

    const deleteButton = taskElement.querySelector(".delete-task");
    deleteButton.addEventListener("click", () => {
        if (confirm("Are you sure you want to delete this task ?")){
        deleteTask(task.id);}

    });

        taskList.appendChild(taskElement);

    
}
function displayTasks() {
    taskList.innerHTML = "";

    tasks.forEach(task => {
        displayTask(task);
    });
}
function detailsHtml(task){
    return `
        <h2>Task Details</h2>

        <div class="detail">

            <h3>Name</h3>
            <p>${task.name}</p>

            <h3>Deadline</h3>
            <p>${task.deadline || "___"}</p>

            <h3>Priority</h3>
            <p>${task.priority}</p>

            <h3>Description</h3>
            <p>${task.description}</p>

            <h3>Status</h3>
            <p>${task.statut}</p>

        </div>
    `;
}

function showTaskDetails(task){
    const taskElement = findTaskElement(task.id);

    if (isMobile()) {
        if (taskElement && taskElement.dataset.panel === "details") {
            closeTaskPanel(taskElement);
            return;
        }
        if (taskElement) {
            openTaskPanel(taskElement, detailsHtml(task), "details");
            return;
        }
    }

    if (asideState.type === "details" && asideState.id === task.id) {
        taskDetails.innerHTML = "";
        asideState = { type: null, id: null };
        return;
    }

    closeTaskPanel(taskElement);
    taskDetails.innerHTML = detailsHtml(task);
    asideState = { type: "details", id: task.id };
}

async function editTask(task) {

    const formHtml = `
        <h2>Edit Task</h2>

        <form class="edit-form">

            <label>
                Name
                <input type="text" name="name" value="${task.name}">
            </label>

            <label>
                Deadline
                <input type="date" name="deadline" value="${task.deadline}">
            </label>

            <label>
                Priority
                <input type="number" name="priority" value="${task.priority}">
            </label>

            <label>
                Description
                <textarea name="description">${task.description}</textarea>
            </label>

            <button type="submit">Save</button>

        </form>
    `;

    const taskElement = findTaskElement(task.id);
    let target = taskDetails;

    if (isMobile() && taskElement) {
        if (taskElement.dataset.panel === "edit") {
            closeTaskPanel(taskElement);
            return;
        }
        target = openTaskPanel(taskElement, formHtml, "edit");
    } else {
        if (asideState.type === "edit" && asideState.id === task.id) {
            taskDetails.innerHTML = "";
            asideState = { type: null, id: null };
            return;
        }
        closeTaskPanel(taskElement);
        taskDetails.innerHTML = formHtml;
        asideState = { type: "edit", id: task.id };
    }

    const form = target.querySelector(".edit-form");
    form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const response = await fetch (`${API_URL}/${task.id}`,
    {method : "PUT",
    headers : {"Content-Type" : "application/json"},
    body: JSON.stringify({
         name: formData.get("name"),
         description: formData.get("description"),
         deadline: formData.get("deadline") || null,
         priority: Number(formData.get("priority")) || null
         })
    });
    await loadTasks();
    const updated = tasks.find(t => t.id === task.id);
    showTaskDetails(updated);

});
}

async function deleteTask(id) {
    await fetch(`${API_URL}/${id}`,
    { method : "DELETE"});

    await loadTasks();
}

addButton.addEventListener("click", () => {
    showAddForm();}
);

filter.addEventListener("change", () => {

    const filterValue = filter.value;

    if (filterValue === "") {
        displayTasks();
        return;
    }

    let filteredTasks;

    if (filterValue === "priority") {
        filteredTasks = [...tasks].sort(
            (a, b) => b.priority - a.priority
        );
    }

    if (filterValue === "deadline") {
        const sortDate = (t) => t.deadline ? new Date(t.deadline).getTime() : Infinity;
        filteredTasks = [...tasks].sort(
            (a, b) => sortDate(a) - sortDate(b)
        );
    }

    if (filterValue === "status") {
        filteredTasks = tasks.filter(
            task => task.statut === "Waiting"
        );
    }

    displayFilteredTasks(filteredTasks);
});

searchInput.addEventListener("input", () => {

    const searchValue = searchInput.value.toLowerCase().trim();

    const results = tasks.filter(task =>
        task.name.toLowerCase().includes(searchValue)
    );

    displayFilteredTasks(results);
});

function displayFilteredTasks(filteredTasks) {

    taskList.innerHTML = "";

    filteredTasks.forEach(task => {
        displayTask(task);
    });
}
function showAddForm() {

    const formHtml = `
    <h2>Add Task</h2>

        <form class="add-form">

            <label>
                Name
                <input type="text" name="name" required>
            </label>

            <label>
                Deadline
                <input type="date" name="deadline">
            </label>

            <label>
                Priority
                <input type="number" name="priority" min="1">
            </label>

            <label>
                Description
                <textarea name="description"></textarea>
            </label>

            <button type="submit">Add Task</button>

        </form>
        `;

    let target = taskDetails;

    if (isMobile()) {
        const existing = taskList.querySelector(":scope > .task-panel--add");
        if (existing) {
            existing.remove();
            return;
        }
        const panel = document.createElement("div");
        panel.className = "task-panel task-panel--add";
        panel.innerHTML = formHtml;
        taskList.prepend(panel);
        target = panel;
    } else {
        if (asideState.type === "add") {
            taskDetails.innerHTML = "";
            asideState = { type: null, id: null };
            return;
        }
        taskDetails.innerHTML = formHtml;
        asideState = { type: "add", id: null };
    }

        const form = target.querySelector(".add-form");
        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            const formData = new FormData(form);

            const newTask = {
                name: formData.get("name"),
                deadline: formData.get("deadline") || null,
                priority: Number(formData.get("priority")) || null,
                description: formData.get("description"),
            };
            const response = await fetch(API_URL,{
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newTask)
            });
            const createdTask = await response.json()
            await loadTasks();
            showTaskDetails(createdTask);
        });
}

loadTasks()