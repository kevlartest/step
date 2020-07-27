/**
 * Load comments from datastore and insert them in the webpage
 * @param {number} amount The maximum amount of comments to fetch and display
 */
async function loadComments(amount){

    // Make sure selected comments amount is kept even when page reloads after submitting a comment:
    // Load previous comments amount value from browser cookies
    let commentsAmount = sessionStorage.getItem("commentsAmount");

    // If an amount was specified with the function, use that amount and store it
    if(typeof amount !== 'undefined'){
        commentsAmount = amount;
    }
    // Otherwise, if no amount was specified and none was stored, set the default value of 5
    else if(commentsAmount == null){
        commentsAmount = 5;
    }

    sessionStorage.setItem("commentsAmount", commentsAmount);

    // Set dropdown to value, so on reloading they're not out of sync
    document.getElementById('comment-amount').value = commentsAmount;

    const comments = await (await fetch('/list-comments?amount=' + commentsAmount)).json()
        .catch(e => {
            alert("There was a problem fetching comments! Please refresh the page");
            console.log(e);
        });

    const commentListElement = document.getElementById('comments-list');
    commentListElement.textContent = ''; // Remove all comments before re-adding specified amount
    comments.forEach((comment) => commentListElement.appendChild(createCommentElement(comment)));
}

/**
 * Creates the HTML list element that contains a single comment, including author, timestamp, body and delete button
 * @param {Object} comment A comment as fetched from datastore, including email, body and timestamp
 * @return {HTMLLIElement} The HTML list element that contains the comment to be displayed
 */
function createCommentElement(comment) {
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    // Convert UNIX seconds to milliseconds
    const timestamp = new Date(comment.timestamp.seconds * 1000);
    
    // Long format the timestamp (e.g. Sunday 9 August 2020, 04:06)
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    const formattedTimestamp = timestamp.toLocaleDateString('en-IE', options);

    // Create a comment heading containing the email address and timestamp
    const commentHeading = document.createElement('p');

    const emailElement = document.createElement('span');
    emailElement.className = 'commentEmail';
    emailElement.innerText = comment.email;

    const timestampElement = document.createElement('span');
    timestampElement.className = 'commentTimestamp';
    timestampElement.innerText = formattedTimestamp;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment)
        .then(() => { // If deleted successfully
            commentElement.remove();
            loadComments();
        })
        .catch(e => alert(e));
    });

    commentHeading.appendChild(emailElement);
    commentHeading.appendChild(timestampElement);
    commentHeading.appendChild(deleteButtonElement);

    // Comment body with the actual text
    const bodyElement = document.createElement('p');
    bodyElement.className = 'commentBody';
    bodyElement.innerText = comment.body;

    commentElement.appendChild(commentHeading);
    commentElement.appendChild(bodyElement);

    return commentElement;
}

async function deleteComment(comment) {
    const args = new URLSearchParams();
    args.append('id', comment.id);
    const response = await fetch('/delete-comment', {method: 'POST', body: args})

    if(response.status === 200) return;
    else throw('There was a problem deleting the comment!');
}

// Disable submit button if body length < 15 characters
function validateComment(){
    const form = document.getElementById('comment-form');
    const bodyLength = form.body.value.length;
    document.getElementById('submit-button').disabled = (bodyLength < 15 || bodyLength > 2000);
}

// Fetch comment form if user is logged in, otherwise ask to login
async function doLogin(){
    const formRequest = await fetch('/login');
    const text = await formRequest.text();
    var element = document.createElement('html');
    element.innerHTML = text;
    document.getElementById('comment-form-div').appendChild(element);
}