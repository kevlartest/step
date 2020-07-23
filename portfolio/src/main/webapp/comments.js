async function loadComments(amount){
    console.log("amount",amount);
    // keep the previous amount if a new one is not specified

    const previousDefined = typeof loadComments.amount !== 'undefined';
    const currentDefined = typeof amount !== 'undefined';

    if(!previousDefined){
        loadComments.amount = currentDefined ? amount : 5;
    } else if(currentDefined){
        loadComments.amount = amount;
    }

    console.log("loadComments amount", loadComments.amount);

    const request = await fetch('/list-comments?amount=' + loadComments.amount);
    const comments = await request.json();
    const commentListElement = document.getElementById('comments-list');
    commentListElement.textContent = ''; // Remove all comments before re-adding specified amount
    comments.forEach((comment) => commentListElement.appendChild(createCommentElement(comment)));
}

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
        deleteComment(comment);
        commentElement.remove(); // Remove this comment element
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

function deleteComment(comment) {
    const args = new URLSearchParams();
    args.append('id', comment.id);
    fetch('/delete-comment', {method: 'POST', body: args})
        .then(response => {
            // Reload the comments only after this one has been deleted
            if(response.status === 200) loadComments()
        });
}