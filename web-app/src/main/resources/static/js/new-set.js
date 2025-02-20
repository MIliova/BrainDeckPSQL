
function autoResizeDiv(el) {
    el.style.height = 'auto'; // Сбрасываем высоту
    el.style.height = el.scrollHeight + 'px'; // Устанавливаем по содержимому
}

// Удаление пустых <br> в абзацах
function removeExtraBreaks(editableDiv) {
    const brs = editableDiv.querySelectorAll('br');
    brs.forEach(br => {
        if (br.parentNode === editableDiv)
            editableDiv.removeChild(br);
    });

    const paragraphs = editableDiv.querySelectorAll('p');
    paragraphs.forEach(paragraph => {
        const children = Array.from(paragraph.childNodes);
        children.forEach(child => {
            if (child.nodeName === 'BR' && !child.nextSibling) {
                paragraph.removeChild(child); // Удаляем <br>, если он последний
            }
        });
        if (paragraph.textContent.trim() === '') {
            paragraph.innerHTML = '<br>';
        }
    });
}

// function obTextInP(el){
//     const selection = window.getSelection();
//     const range = selection.getRangeAt(0);
//     const currentBlock = range.startContainer.nodeType === Node.TEXT_NODE
//         ? range.startContainer.parentNode
//         : range.startContainer;
//     if (currentBlock.tagName !== 'P'){
//
//             const newParagraph = document.createElement('P');
//             newParagraph.textContent = range.startContainer.textContent;
//             el.appendChild(newParagraph);
//
//
//
//         range.startContainer.textContent = '';
//         const newRange = document.createRange();
//         newRange.setStart(newParagraph, 0);
//         newRange.collapse(true);
//         selection.removeAllRanges();
//         selection.addRange(newRange);
//     }
// }

function insertParagraphAfterCurrent(el) {
    const selection = window.getSelection();
    const range = selection.getRangeAt(0);
    const newParagraph = document.createElement('P');
    newParagraph.innerHTML = '<br>';

    const currentBlock = range.startContainer.nodeType === Node.TEXT_NODE
        ? range.startContainer.parentNode
        : range.startContainer;

    if (currentBlock.tagName === 'P') {
        //const selectedText = range.toString();
        const beforeCursorText = range.startContainer.textContent.slice(0, range.startOffset).trim();
        const afterCursorText = range.startContainer.textContent.slice(range.startOffset).trim();

        currentBlock.parentNode.insertBefore(newParagraph, currentBlock.nextSibling);

        if (afterCursorText.length > 0)
            newParagraph.textContent = afterCursorText;

        if (beforeCursorText.length > 0){
            range.startContainer.textContent = beforeCursorText;
        } else {
            range.startContainer.textContent = '';
            currentBlock.innerHTML = '<br>';
        }
    } else {
        el.appendChild(newParagraph);
    }

    // Перемещаем курсор в новый абзац
    const newRange = document.createRange();
    newRange.setStart(newParagraph, 0);
    newRange.collapse(true);
    selection.removeAllRanges();
    selection.addRange(newRange);
}

const languageMenu = document.querySelector('.language-menu-outer');
//const termsObj = [];
const termsObjNew = [];
function setLanguage(el, id) {
    let termsObjNewType = 'description';
    if (languageMenu.owner.dataset.language === 'term') {
        termsObjNewType = 'term';
        document.getElementById('field-term-language').value = id;
    } else {
        document.getElementById('field-description-language').value = id;
    }
    languageMenu.owner.className="language-button";
    languageMenu.style.display = 'none';
    languageMenu.owner.innerText = el.innerText;
    termsObjNew.forEach(obj => {
        if (obj.type == termsObjNewType) {
            const placeholder = obj.el.getAttribute('data-placeholder');
            obj.el.setAttribute('data-placeholder', obj.el.getAttribute('data-base-placeholder') + ' ' + el.innerText);
            if (obj.el.textContent.trim() === placeholder) {
                obj.el.textContent = obj.el.getAttribute('data-placeholder');
                obj.el.classList.add(obj.el.getAttribute('data-placeholder-class'));
            }
        }
    })
}


document.addEventListener('DOMContentLoaded', function () {
    const nameInp = document.getElementById('field-title');
    nameInp.addEventListener('keyup', (event) => {
        if (nameInp.value.length > 0)
            nameInp.previousElementSibling.innerHTML = nameInp.previousElementSibling.dataset.message;//"Title";
        else
            nameInp.previousElementSibling.innerHTML="&nbsp;";
    });

    const descrInp = document.getElementById('field-description');
    descrInp.addEventListener('keyup', (event) => {
        if (descrInp.value.length > 0)
            descrInp.previousElementSibling.innerHTML=descrInp.previousElementSibling.dataset.message;//"Description";
        else
            descrInp.previousElementSibling.innerHTML="&nbsp;";
    });




    let i = 0;
    let term = document.getElementById('term'+i);
    let descr = document.getElementById('description'+i);
    while (term  && descr) {
        let atrId = term.getAttribute("data-id");
        //termsObj.push({t:term, d:descr});
        termsObjNew.push({el:term, type:'term'})
        termsObjNew.push({el:descr, type:'description'})
        if (atrId) {
            //termsObj[termsObj.length-1].id = atrId;
            termsObjNew[termsObjNew.length-2].id = atrId;
            termsObjNew[termsObjNew.length-1].id = atrId;
        }
        i++;
        term.innerHTML = term.innerText.split('\n')
            .map(p=> '<p>' + (p.length>0?p:'<br>') + '</p>')
            .join('');
        descr.innerHTML = descr.innerText.split('\n')
            .map(p=> '<p>' + (p.length>0?p:'<br>') + '</p>')
            .join('');
        term = document.getElementById('term'+i);
        descr = document.getElementById('description'+i);
    }



    //const tis = document.querySelectorAll('.term-input');
    termsObjNew.forEach(to => {
        const ti=to.el;
        ti.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                insertParagraphAfterCurrent(ti);
                autoResizeDiv(ti);
            }
        });
        ti.addEventListener('input', (event) => {
            //obTextInP(ti);
            const content = ti.innerHTML;
            // Проверка, если последний элемент не <p>, то добавляем новый
            if (!content.endsWith('</p>')) {
                insertParagraphAfterCurrent(ti);
            }
            removeExtraBreaks(ti);
            // Проверка на удаление тега <p>
            const paragraphs = ti.querySelectorAll('p');
            if (paragraphs.length === 1 && paragraphs[0].innerHTML.trim() === '') {
                console.log('удаление тега <p>');
                paragraphs[0].remove(); // Удаляем пустой абзац, если он один
            }
            autoResizeDiv(ti);
        });

        ti.addEventListener('focus', () => {
            if (ti.textContent.trim() === ti.getAttribute('data-placeholder')) {
                ti.textContent = '';
                ti.classList.remove(ti.getAttribute('data-placeholder-class'));
                ti.innerHTML='<p><br></p>';
                const p = ti.querySelector('p');
                const range=document.createRange();
                const sel=window.getSelection();
                range.setStart(p,0);
                range.collapse(true);
                sel.removeAllRanges();
                sel.addRange(range);
            }
        });
        ti.addEventListener('blur', () => {
            if (ti.textContent.trim() === '') {
                ti.textContent = ti.getAttribute('data-placeholder');
                ti.classList.add(ti.getAttribute('data-placeholder-class'));
            }
        });
        // Инициализация плейсхолдера при загрузке страницы
        if (ti.textContent.trim() === '') {
            ti.textContent = ti.getAttribute('data-placeholder');
            ti.classList.add(ti.getAttribute('data-placeholder-class'));
        }


    });
    const lbs = document.querySelectorAll('.language-button');
    lbs.forEach(lb => {
        lb.style.display = 'block';
        //lb.nextElementSibling.style.display = 'none';
        lb.addEventListener('click', (event) => {
            languageMenu.owner = lb;
            languageMenu.style.display = 'block';
            const buttonRect = lb.getBoundingClientRect();
            languageMenu.style.top = `${buttonRect.bottom + window.scrollY}px`;
            languageMenu.style.left = `${buttonRect.left + window.scrollY}px`;
        });
    });
});

document.addEventListener('click', (event) => {
    if (!languageMenu.contains(event.target) && !event.target.classList.contains('language-button')) {
        languageMenu.style.display = 'none'; // Скрыть меню
    }
});


function setFormValidate(){

    const errors = [];

    const fieldTitle = document.getElementById('field-title');
    if (fieldTitle.value === "") {
        errors.push(fieldTitle.dataset.message);
        fieldTitle.parentElement.parentElement.className = "blue-row-err";
    } else {
        fieldTitle.parentElement.parentElement.className = "";
    }

    const fieldDescription = document.getElementById('field-description');
    if (fieldDescription.value === "") {
        errors.push(fieldDescription.dataset.message);
        fieldDescription.parentElement.parentElement.className = "blue-row-err";
    } else {
        fieldDescription.parentElement.parentElement.className = "";
    }

    const fieldTermLanguage = document.getElementById('field-term-language');
    if (fieldTermLanguage.value === "") {
        errors.push(fieldTermLanguage.dataset.message);
        document.getElementById('language-button-term').className = "language-button language-button-err";
    } else {
        document.getElementById('language-button-term').className = "language-button";
    }

    const fieldDescriptionLanguage = document.getElementById('field-description-language');
    if (fieldDescriptionLanguage.value === "") {
        errors.push(fieldDescriptionLanguage.dataset.message);
        document.getElementById('language-button-description').className = "language-button language-button-err";
    } else {
        document.getElementById('language-button-description').className = "language-button";
    }

    // for (let i = 0; i < termsObj.length; i++) {
    //     if (!termsObj[i].t.textContent.trim() && !termsObj[i].d.textContent.trim()) {
    //         termsObj[i].t.parentElement.parentElement.parentElement.className = "blue-row-err";
    //         errors.push(document.getElementById('field-terms').dataset.message); //errors.push("add at least three terms");
    //         break;
    //     } else {
    //         termsObj[i].t.parentElement.parentElement.parentElement.className = "";
    //     }
    // }

    for (let i = 0; i < termsObjNew.length; i++) {
        if (!termsObjNew[i].el.textContent.trim() && !termsObjNew[i+1].el.textContent.trim()) {
            termsObjNew[i].el.parentElement.parentElement.parentElement.className = "blue-row-err";
            errors.push(document.getElementById('field-terms').dataset.message); //errors.push("add at least three terms");
            break;
        } else {
            termsObjNew[i].el.parentElement.parentElement.parentElement.className = "";
        }
        i++;
    }

    if (errors.length > 0) {
        setFormSetErrors(errors.join(", "));
        //return false;
    }
    return true;
}
function setFormSetErrors (text) {
    const fieldError = document.getElementById('field-error');
    fieldError.innerHTML = fieldError.dataset.message + " " + text;
    fieldError.style.display = 'block';
}
function setFormGetTerms() {
    const termsDto = [];
    const setIdField = document.getElementById('field-id');
    const setId= setIdField ? setIdField.value : 0;

    // for (let i = 0; i < termsObj.length; i++) {
    //     const term = Array.from(termsObj[i].t.querySelectorAll("p"))
    //         .map(p => p.textContent)
    //         .join("\n").trim();
    //     const description = Array.from(termsObj[i].d.querySelectorAll("p"))
    //         .map(p => p.textContent)
    //         .join("\n").trim();
    //     if (term !== "" || description !== "") {
    //         newTerms.push({
    //             term: term,
    //             description: description
    //         });
    //         if (termsObj[i].id) {
    //             newTerms[newTerms.length - 1].id = termsObj[i].id;
    //             newTerms[newTerms.length - 1].setId = setId;
    //
    //
    //         }
    //     }
    // }

    for (let i = 0; i < termsObjNew.length; i++) {
        const term = Array.from(termsObjNew[i].el.querySelectorAll("p"))
            .map(p => p.textContent)
            .join("\n").trim();
        const description = Array.from(termsObjNew[i+1].el.querySelectorAll("p"))
            .map(p => p.textContent)
            .join("\n").trim();

        if (term !== "" || description !== "") {
            termsDto.push({
                term: term,
                description: description
            });
            if (termsObjNew[i].id) {
                termsDto[termsDto.length - 1].id = termsObjNew[i].id;
                termsDto[termsDto.length - 1].setId = setId;
            }
        }
        i++;
    }

    return termsDto;
}
const submitType = '';
document.getElementById('submitButton').addEventListener("click",
    function (event) {

    if (!setFormValidate())
        return false;

    switch (submitType) {
        case 'Data':
            submitFormData(event);
            break;
        case 'JSON':
            submitFormJSON();
            break;
        default:
            const termsDto = setFormGetTerms();

            document.getElementById("field-terms").value = JSON.stringify(termsDto);

            document.getElementById('set-form').submit();

            break
    }
});

function submitFormData(event){

    event.preventDefault();

    const formData = new FormData();
    formData.append("title", document.getElementById('field-title').value);
    formData.append("description", document.getElementById('field-description').value);
    formData.append("termLanguageId", document.getElementById('field-term-language').value);
    formData.append("descriptionLanguageId", document.getElementById('field-description-language').value);

    const termsDto = setFormGetTerms();
    //const termsField = document.getElementById("field-terms");
    //termsField.value = JSON.stringify(newTerms);
    formData.append('terms', new Blob([JSON.stringify(termsDto)], { type: 'application/json' }));
    //formData.append("terms", JSON.stringify(newTerms));

    fetch(document.getElementById('set-form').action,{
               method: "POST",
               body:formData
            })
            .then(response => response.json())
            .then(data => console.log("Ответ сервера", data))
            .catch(error => console.error("Ошибка", error));
}

function submitFormJSON(){

    const jsonData = {
            title: document.getElementById('field-title').value,
            description: document.getElementById('field-description').value,
            termLanguageId: document.getElementById('field-term-language').value,
            descriptionLanguageId: document.getElementById('field-description-language').value,
            terms: setFormGetTerms()
        };

        fetch('/api/sets/create',{
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData)
            })
            .then(response => {
                return response.text().then(text=>{
                    console.log("Ответ сервера text", text);
                    return JSON.parse(text);
                })
            })
            .then(data => {
                if (data.errors) {
                    //console.log("Ответ сервера errors", Object.values( data.errors).join(', '));
                    setFormSetErrors(Object.values( data.errors).join(', '));
                }
                console.log("Ответ сервера data", data);
            })
            .catch(error => {
                console.error("Ответ сервера Ошибка", error);

            });
}



//console.log(event.target.classList);
