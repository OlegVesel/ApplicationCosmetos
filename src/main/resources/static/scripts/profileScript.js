function edit(elementId){

    if (document.getElementById(elementId).hasAttribute('readonly')){
        document.getElementById(elementId).removeAttribute('readonly')
        if (document.getElementById('save').hasAttribute('disabled')){
                document.getElementById('save').removeAttribute('disabled')
                document.getElementById('cancel').removeAttribute('disabled')
        }
    }
    else{
       document.getElementById(elementId).setAttribute('readonly',true)
       if (document.getElementById('email').hasAttribute('readonly') &&
            document.getElementById('username').hasAttribute('readonly')){
                        document.getElementById('save').setAttribute('disabled',true)
                        document.getElementById('cancel').setAttribute('disabled',true)
        }
    }

}

function activateSaveButton(){
    document.getElementById('save').removeAttribute('disabled')
    document.getElementById('cancel').removeAttribute('disabled')
}

function disabledAll(){
    document.getElementById('email').setAttribute('readonly',true)
    document.getElementById('username').setAttribute('readonly',true)
    document.getElementById('save').setAttribute('disabled',true)
    document.getElementById('cancel').setAttribute('disabled',true)


}