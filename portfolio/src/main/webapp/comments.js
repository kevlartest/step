const MIN_COMMENT_LENGTH = 15;
const MAX_COMMENT_LENGTH = 2000;
const MIN_NICKNAME_LENGTH = 3;
const MAX_NICKNAME_LENGTH = 20;

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

    const [comments, loginData] = await Promise.all([
        await (await fetch('/list-comments?amount=' + commentsAmount)).json(),
         await getLoginData()
        ])
        .catch(e => {
            alert("There was a problem fetching comments! Please refresh the page");
            console.log(e);
        });

    const commentListElement = document.getElementById('comments-list');
    commentListElement.textContent = ''; // Remove all comments before re-adding specified amount
    comments.forEach((comment) => commentListElement.appendChild(createCommentElement(comment, loginData)));
}

/**
 * Creates the HTML list element that contains a single comment, including author, timestamp, body and delete button
 * @param {Object} comment A comment as fetched from datastore, including userId, body and timestamp
 * @param {Object} loginData The user login data, i.e. whether they're logged in, an admin, and their userId
 * @return {HTMLLIElement} The HTML list element that contains the comment to be displayed
 */
function createCommentElement(comment, loginData) {
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    const commentHeading = document.createElement('p');

    createCommentNicknameElement(comment.userId).then(e => commentHeading.insertAdjacentElement('afterbegin', e));
    createCommentTimestampElement(comment.timestamp.seconds).then(e => commentHeading.appendChild(e));

    // Only show delete button if comment was made by logged-in user, or they're an admin
    if(loginData.isUserAdmin || (loginData.loggedIn && loginData.userId === comment.userId)){
        createCommentDeleteButtonElement(comment, commentElement).then(e => commentHeading.appendChild(e));
    }

    commentElement.appendChild(commentHeading);
    createCommentBody(comment.body).then(e => commentElement.appendChild(e));

    return commentElement;
}

async function createCommentBody(body){
    const bodyElement = document.createElement('p');
    bodyElement.className = 'commentBody';
    bodyElement.innerText = body;

    return bodyElement;
}

async function createCommentDeleteButtonElement(comment, commentElement){
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.className = 'deleteButton';
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment)
        .then(() => { // If deleted successfully
            commentElement.remove();
            loadComments();
        })
        .catch(e => alert(e));
    });
    return deleteButtonElement;
}

async function createCommentTimestampElement(secondsTimestamp){
    // Convert UNIX seconds to milliseconds
    const timestamp = new Date(secondsTimestamp * 1000);
    
    // Long format the timestamp (e.g. Sunday 9 August 2020, 04:06)
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    const formattedTimestamp = timestamp.toLocaleDateString('en-IE', options);

    const timestampElement = document.createElement('span');
    timestampElement.className = 'commentTimestamp';
    timestampElement.innerText = "<" + formattedTimestamp + ">";

    return timestampElement;
}

async function createCommentNicknameElement(userId){
    const nicknameElement = document.createElement('span');
    nicknameElement.className = 'commentNickname';

    try {
        const response = await (await fetch('/nickname?userId=' + userId)).json();
        nicknameElement.innerText = response.nickname;
        console.log("Nickname", response);
    } catch (e){
        nicknameElement.innerText = "UNDEFINED";
        console.log("Failed to load nickname for " + userId);
    }

    return nicknameElement;    
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
    document.getElementById('submit-button').disabled = (bodyLength < MIN_COMMENT_LENGTH || bodyLength > MAX_COMMENT_LENGTH);
}

// Fetch comment form if user is logged in, otherwise ask to login
async function doLogin(){
    const formRequest = await fetch('/login');
    const text = await formRequest.text();
    var element = document.createElement('html');
    element.innerHTML = text;
    document.getElementById('comment-form-div').appendChild(element);

    getLoginData();
}

// Get whether user is logged in, and their userId
// i.e. { "isLoggedIn": true, "userId": "39572937652947642" }
async function getLoginData(){
    const request = await fetch('/logindata');
    return await request.json();
}

// Disable nickname form if length restrictions are not met
function validateNickname(){
    const form = document.getElementById('nickname-form');
    const nickname = form.nickname.value.length;
    document.getElementById('nickname-submit-button').disabled = (nickname < MIN_NICKNAME_LENGTH || nickname > MAX_NICKNAME_LENGTH);
}
