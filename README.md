<h1>Problemas e Soluções</h1><br>
Aplicativo criado com o intuito de gerar relatórios para atendimentos à lojas podendo fiscalizar onde está o problema como por exemplo locais que necessitam de manutenção,
ambientes necessitando de limpeza entre outras opções, tendo também uma tela para a visualização somente dos problemas e outra para reportar os problemas.

Possuindo duas opções na tela incial, Auditor e Vendedor.

<h2>Auditor</h2>
<br>
Selecionando a opção de auditor o usuário é direcionado a uma tela de login, exigindo um email e senha que estão cadastrados no FireBase.
<h3>Ações</h3>
Com o Login efetuado o usuário é levado a tela de seleção de Região e Loja, tendo métodos que define qual loja o usuário será capaz de selecionar após especificar a região.
<br>
Após isso abre uma tela de formulário, que solicita o uso da cãmera do smartphone, e em algumas opções, no caso do script salvo, "Necessita Limpeza?", "Freezer com Invasão" e " Necessita de manutenção", que ao selecionar sua checkbox abre uma visão com um botão de câmera e de salvar, que após retirar a foto ela irá aparecer no local onde antes estava um ImageView somente com a moldura.
Após tendo selecionado os checkbox necessários e ter retirado as fotos necessárias o app salva no firebase o formulário contendo em cada foto o nome da loja selecionada, o UID do usuário, e qual foi o problema encontrado.
<br>
<br>
<h2>Vendedor</h2>
<br>
Já na tela de vendedores o Login é efetuado através do método de autenticação pelo número de telefone.
Tendo que digitar o seu número no campo de texto e após isso clicar no botão para verificar o número, com a verificação concluída o usuário receberá um sms com um código de verificação único. Sendo necessário digitar no campo do código de verificação e somente depois disso efetuar o login que irá verificar se o código está correto e somente após isso o usuário terá sua tela alterada.
<br>
<h3>Ações</h3>
<ul>
  <li>Login </li>
  <li>Seleção de Região</li>
  <li>Seleção de Ponto de Venda</li>
  <li>Exibição de Problemas Encontrados</li>
  <li>Solução do Problema exibido</li>
</ul>
<br>

-Com o login realizado não é necessário realizar o cadastro novamente, somente se o usuário selecionar o botão de sair de sua conta.

-A seleção de região é definida de acordo com quais regiões foram cadastradas no FireBase.

-Para selecionar o ponto de venda o usuário precisa anteriormente ter selecionado a região, pois somente depois de selecionar é liberado quais lojas estão cadastradas nessa região.

-Com o ponto de venda selecionado, os dados carregados com problemas são exibidos em um RecyclerView com a imagem e qual problema foi encontrado no FireBase.

-Após localizar o problema, o usuário solucionará o que aconteceu e reenviará uma foto nova porém dessa vez com o problema solucionado.








