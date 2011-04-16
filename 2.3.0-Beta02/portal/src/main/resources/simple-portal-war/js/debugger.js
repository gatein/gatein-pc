function showDetails(element,target) {
   try{
      element.style.backgroundColor = '#ffffdd';
      $(target).style.display = '';
   }catch(e){

   }
}

function hideDetails(element,target) {
   try{
      element.style.backgroundColor = '#fff';
      $(target).style.display = 'none';
      //Effect.BlindUp(target);
      //return true;
   }catch(e){

   }
}

function showAllDetails() {
   var targets = document.getElementsByClassName('details');
   for (var i=0; i < targets.length; i++){
      try{
         targets[i].style.display = '';
         targets[i].onmouseover = '';
      }catch(e){
//               alert(e);
      }
   }
}

window.onload = function(){

   var targets = document.getElementsByClassName('details');
   for (var i=0; i < targets.length; i++){
      try{
         targets[i].style.display = 'none';
      }catch(e){
//               alert(e);
      }
   }
};