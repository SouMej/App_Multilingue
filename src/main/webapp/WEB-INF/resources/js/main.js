'use strict';

function togglePwd(id) {
  var f = document.getElementById(id);
  if (f) f.type = f.type === 'password' ? 'text' : 'password';
}

function clearSearch() {
  var f = document.getElementById('sInput');
  if (f) { f.value = ''; f.focus(); }
}

document.addEventListener('DOMContentLoaded', function () {

  /* Indicateur de force du mot de passe */
  var pwd  = document.getElementById('password');
  var fill = document.getElementById('str-fill');
  var lbl  = document.getElementById('str-lbl');

  if (pwd && fill) {
    pwd.addEventListener('input', function () {
      var s     = strength(this.value);
      var pct   = [0,25,50,75,100][s];
      var color = ['#E5E7EB','#EF4444','#F59E0B','#3B82F6','#10B981'][s];
      var text  = ['','Faible','Moyen','Bien','Fort'][s];
      fill.style.width      = pct + '%';
      fill.style.background = color;
      if (lbl) lbl.textContent = text;
    });
  }

  /* Validation confirmation mot de passe */
  var conf = document.getElementById('confirmPassword');
  if (pwd && conf) {
    conf.addEventListener('blur', function () {
      this.classList.toggle('input-err', this.value !== '' && this.value !== pwd.value);
    });
  }

  /* Fermer les alertes au clic */
  document.querySelectorAll('.alert').forEach(function (el) {
    el.style.cursor = 'pointer';
    el.addEventListener('click', function () {
      this.style.transition = 'opacity .3s';
      this.style.opacity = '0';
      setTimeout(function () { el.remove(); }, 300);
    });
  });

});

function strength(p) {
  if (!p || p.length < 8) return 1;
  var u = /[A-Z]/.test(p), d = /[0-9]/.test(p), s = /[^A-Za-z0-9]/.test(p);
  if (u && d && s) return 4;
  if ((u && d)||(u && s)||(d && s)) return 3;
  if (u || d) return 2;
  return 1;
}
