// Home page functionality
document.addEventListener('DOMContentLoaded', () => {
    setMaxDate();
    loadLast7DaysExpenses();
    setupEventListeners();
    loadFinancialSummary();
});

// Set maximum date to today
function setMaxDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('expenseDate').max = today;
    document.getElementById('modifyStartDate').max = today;
    document.getElementById('modifyEndDate').max = today;
}

// Setup event listeners
function setupEventListeners() {
    // New Expense Form
    document.getElementById('newExpenseForm').addEventListener('submit', createExpense);
    document.getElementById('expenseComments').addEventListener('input', updateWordCount);

    // Search Expense
    document.getElementById('searchExpenseBtn').addEventListener('click', searchExpenses);
    document.getElementById('modifyStartDate').addEventListener('change', toggleSearchButton);

    // New Revenue Form
    document.getElementById('newRevenueForm').addEventListener('submit', createRevenue);
    document.getElementById('revenueComments').addEventListener('input', updateWordCount);
    document.getElementById('revenueRecurring').addEventListener('change', toggleFrequencyDropdown);

    // Edit Modal
    document.getElementById('saveEditBtn').addEventListener('click', saveEditedExpense);

    // View tab
    document.getElementById('viewTab').addEventListener('shown.bs.tab', loadLast7DaysExpenses);
}

// Create Expense
async function createExpense(e) {
    e.preventDefault();

    const category = document.getElementById('expenseCategory').value;
    const amount = document.getElementById('expenseAmount').value;
    const date = document.getElementById('expenseDate').value;
    const comments = document.getElementById('expenseComments').value;

    if (!category || !amount || !date) {
        showError('Please fill all required fields');
        return;
    }

    try {
        const response = await fetch('/api/expenses', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({
                category,
                amount: parseFloat(amount),
                transactionDate: date,
                comments
            })
        });

        const data = await response.json();

        if (data.success) {
            showSuccess('Expense added successfully!');
            document.getElementById('newExpenseForm').reset();
            setTimeout(() => loadLast7DaysExpenses(), 1000);
        } else {
            showError(data.error || 'Failed to create expense');
        }
    } catch (error) {
        showError('Error creating expense: ' + error.message);
    }
}

// Search Expenses
async function searchExpenses() {
    const startDate = document.getElementById('modifyStartDate').value;
    const endDate = document.getElementById('modifyEndDate').value;

    if (!startDate) {
        showError('Start date is required');
        return;
    }

    try {
        let url = `/api/expenses/search?startDate=${startDate}`;
        if (endDate) {
            url += `&endDate=${endDate}`;
        }

        const response = await fetch(url, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        const data = await response.json();

        if (data.success) {
            displayExpenseTable(data.data, 'expenseTableBody');
            document.getElementById('expenseTableContainer').style.display = 'block';
            document.getElementById('noExpensesMessage').style.display = data.data.length === 0 ? 'block' : 'none';
        } else {
            showError(data.error);
        }
    } catch (error) {
        showError('Error searching expenses: ' + error.message);
    }
}

// Load Last 7 Days Expenses
async function loadLast7DaysExpenses() {
    try {
        const response = await fetch('/api/expenses/last-7-days', {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        const data = await response.json();

        if (data.success) {
            displayExpenseTable(data.data, 'viewTableBody');
            const total = data.data.reduce((sum, exp) => sum + parseFloat(exp.amount), 0);
            document.getElementById('sevenDayTotal').textContent = '$' + total.toFixed(2);
        } else {
            showError(data.error);
        }
    } catch (error) {
        showError('Error loading expenses: ' + error.message);
    }
}

// Display Expense Table
function displayExpenseTable(expenses, tableBodyId) {
    const tbody = document.getElementById(tableBodyId);
    tbody.innerHTML = '';

    expenses.forEach(expense => {
        const row = document.createElement('tr');
        const modifiedDate = expense.modifiedDate ? new Date(expense.modifiedDate).toLocaleString('en-US', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        }).replace(/(\d{4})-(\d{2})-(\d{2})/, '$3/$2/$1') : '—';

        const status = expense.modifiedDate ? 'Modified' : 'Original';

        let actionButtons = `<button class="btn btn-sm btn-warning" onclick="editExpense('${expense.expenseId}', '${expense.category}', ${expense.amount}, '${expense.transactionDate}', '${expense.comments || ''}')">Edit</button>
                            <button class="btn btn-sm btn-danger" onclick="deleteExpense('${expense.expenseId}')">Delete</button>`;

        row.innerHTML = `
            <td>${expense.category.replace(/_/g, '/').replace('/', '/')}</td>
            <td>$${parseFloat(expense.amount).toFixed(2)}</td>
            <td>${new Date(expense.transactionDate).toLocaleString()}</td>
            <td>${new Date(expense.createdDate).toLocaleString()}</td>
            ${tableBodyId === 'expenseTableBody' ? `<td>${modifiedDate}</td><td>${status}</td>` : ''}
            <td class="action-buttons">${actionButtons}</td>
        `;
        tbody.appendChild(row);
    });
}

// Edit Expense
function editExpense(expenseId, category, amount, date, comments) {
    document.getElementById('editExpenseId').value = expenseId;
    document.getElementById('editCategory').value = category;
    document.getElementById('editAmount').value = amount;
    
    // Extract date in YYYY-MM-DD format
    // Handle both "YYYY-MM-DD HH:MM:SS" and "YYYY-MM-DD" formats
    const dateOnly = date.includes(' ') ? date.split(' ')[0] : date;
    document.getElementById('editDate').value = dateOnly;
    
    document.getElementById('editComments').value = comments || '';

    const modal = new bootstrap.Modal(document.getElementById('editModal'));
    modal.show();
}

// Save Edited Expense
async function saveEditedExpense() {
    const expenseId = document.getElementById('editExpenseId').value;
    const category = document.getElementById('editCategory').value;
    const amount = document.getElementById('editAmount').value;
    const date = document.getElementById('editDate').value;
    const comments = document.getElementById('editComments').value || '';

    if (!category || !amount || !date) {
        showError('Please fill all required fields');
        return;
    }

    try {
        const response = await fetch(`/api/expenses/${expenseId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({
                category: category,
                amount: parseFloat(amount),
                transactionDate: date,
                comments: comments
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            showError(errorData.error || 'Failed to update expense');
            return;
        }

        const data = await response.json();

        if (data.success) {
            showSuccess('Expense updated successfully!');
            // Close the modal properly
            const modalElement = document.getElementById('editModal');
            // Hide modal using Bootstrap
            const bsModal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
            bsModal.hide();
            
            // Wait for modal to close before refreshing
            setTimeout(() => {
                // Refresh both lists
                loadLast7DaysExpenses();
                searchExpenses();
            }, 500);
        } else {
            showError(data.error || 'Failed to update expense');
        }
    } catch (error) {
        console.error('Error updating expense:', error);
        showError('Error updating expense: ' + error.message);
    }
}

// Delete Expense
async function deleteExpense(expenseId) {
    if (!confirm('Are you sure you want to delete this expense?')) {
        return;
    }

    try {
        const response = await fetch(`/api/expenses/${expenseId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        const data = await response.json();

        if (data.success) {
            showSuccess('Expense deleted successfully!');
            searchExpenses();
            loadLast7DaysExpenses();
        } else {
            showError(data.error);
        }
    } catch (error) {
        showError('Error deleting expense: ' + error.message);
    }
}

// Create Revenue
async function createRevenue(e) {
    e.preventDefault();

    const source = document.getElementById('revenueSource').value;
    const amount = document.getElementById('revenueAmount').value;
    const date = document.getElementById('revenueDate').value;
    const isRecurring = document.getElementById('revenueRecurring').checked;
    const frequency = document.getElementById('revenueFrequency').value;
    const notes = document.getElementById('revenueNotes').value;

    if (!source || !amount || !date) {
        showError('Please fill all required fields');
        return;
    }

    if (isRecurring && !frequency) {
        showError('Please select recurring frequency');
        return;
    }

    try {
        const response = await fetch('/api/revenues', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({
                source,
                amount: parseFloat(amount),
                transactionDate: date,
                isRecurring,
                recurringFrequency: frequency,
                notes
            })
        });

        const data = await response.json();

        if (data.success) {
            showSuccess('Revenue added successfully!');
            document.getElementById('newRevenueForm').reset();
            loadFinancialSummary();
        } else {
            showError(data.error || 'Failed to create revenue');
        }
    } catch (error) {
        showError('Error creating revenue: ' + error.message);
    }
}

// Load Financial Summary
async function loadFinancialSummary() {
    try {
        const response = await fetch('/api/financial-summary/current-month', {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        if (!response.ok) {
            return; // API endpoint not yet implemented
        }

        const data = await response.json();

        if (data.success) {
            document.getElementById('totalRevenue').textContent = '$' + parseFloat(data.data.totalRevenue).toFixed(2);
            document.getElementById('totalExpenses').textContent = '$' + parseFloat(data.data.totalExpenses).toFixed(2);
            const remaining = parseFloat(data.data.remainingFunds);
            const remainingElement = document.getElementById('remainingFunds');
            remainingElement.textContent = '$' + remaining.toFixed(2);
            remainingElement.className = 'summary-value ' + (remaining >= 0 ? 'positive' : 'negative');
        }
    } catch (error) {
        console.log('Financial summary not yet available');
    }
}

// Toggle Frequency Dropdown
function toggleFrequencyDropdown() {
    const isChecked = document.getElementById('revenueRecurring').checked;
    document.getElementById('frequencyGroup').style.display = isChecked ? 'block' : 'none';
}

// Toggle Search Button
function toggleSearchButton() {
    const hasStartDate = document.getElementById('modifyStartDate').value;
    document.getElementById('searchExpenseBtn').disabled = !hasStartDate;
}

// Update Word Count
function updateWordCount(e) {
    const textarea = e.target;
    const wordCountElement = textarea.id === 'expenseComments' 
        ? document.getElementById('expenseWordCount')
        : document.getElementById('revenueWordCount');
    
    const words = textarea.value.split(/\s+/).filter(w => w.length > 0).length;
    wordCountElement.textContent = Math.min(words, 100);
}

// Logout
async function logout(event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    
    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        localStorage.removeItem('authToken');
        window.location.href = '/login';
    } catch (error) {
        console.error('Logout error:', error);
        localStorage.removeItem('authToken');
        window.location.href = '/login';
    }
}

// Utility Functions
function getToken() {
    return localStorage.getItem('authToken') || '';
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000);
}

function showSuccess(message) {
    const successDiv = document.getElementById('successMessage');
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    setTimeout(() => {
        successDiv.style.display = 'none';
    }, 3000);
}
