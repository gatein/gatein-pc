
      function collapse(e,target) {
         e.hide();
         new Effect.BlindUp(target + '-target', {queue: {position:'front', scope: 'expandscope', limit:1} });
         Effect.Appear('expand-' + target, { duration: 0.0 });
      }

      function expand(e,target) {
         e.hide();
         Effect.BlindDown(target + '-target', {queue: {position:'end', scope: 'expandscope', limit:1} });
         Effect.Appear('contract-' + target, { duration: 0.0 });
      }

      function collapseAll() {
         var targets = document.getElementsByClassName('target');
         for (var i=0; i < targets.length; i++){
            try{
               var target = targets[i].id.substring(0,targets[i].id.lastIndexOf('-target'));
               $('contract-' + target).hide();
               new Effect.BlindUp(target + '-target');
               Effect.Appear('expand-' + target, { duration: 0.0 });
            }catch(e){
//               alert(e);
            }
         }
      }

      function expandAll() {
        var targets = document.getElementsByClassName('target');
         for (var i=0; i < targets.length; i++){
            try{
               var target = targets[i].id.substring(0,targets[i].id.lastIndexOf('-target'));
               $('expand-' + target).hide();
               new Effect.BlindDown(target + '-target');
               Effect.Appear('contract-' + target, { duration: 0.0 });
            }catch(e){
//               alert(e);
            }
         }
      }


      //collapse all on page load
      window.onload = function(){
//         collapseAll()
         var targets = document.getElementsByClassName('target');
         for (var i=0; i < targets.length; i++){
            try{
               var target = targets[i].id.substring(0,targets[i].id.lastIndexOf('-target'));
               $('contract-' + target).hide();
               new Effect.BlindUp(target + '-target', { duration: 0.0 });
               Effect.Appear('expand-' + target, { duration: 0.0 });
            }catch(e){
//               alert(e);
            }
         }
      };