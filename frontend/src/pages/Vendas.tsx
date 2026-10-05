import React, { useState } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ShoppingCart, Trash2, CheckCircle } from "lucide-react";

export function Vendas() {
  const [itens, setItens] = useState<any[]>([]);
  const [produtoId, setProdutoId] = useState("");
  const [quantidade, setQuantidade] = useState("1");
  const [precoBase, setPrecoBase] = useState("");
  const [desconto, setDesconto] = useState("0");
  const [sucesso, setSucesso] = useState(false);

  const formatterBRL = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

  const { data: produtos } = useQuery({
    queryKey: ["produtos"],
    queryFn: async () => {
      const res = await api.get("/produtos?size=100");
      return res.data.content;
    },
  });

  const mutationVenda = useMutation({
    mutationFn: async (dados: any) => api.post("/vendas", dados),
    onSuccess: () => {
      setSucesso(true);
      setItens([]);
      setTimeout(() => setSucesso(false), 3000);
    },
  });

  const adicionarItem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!produtoId) return;
    const prod = produtos.find((p: any) => p.id === Number(produtoId));
    if (!prod) return;

    setItens([...itens, {
      produtoId: prod.id,
      nome: prod.nome,
      quantidade: Number(quantidade),
      precoBasePraticado: Number(precoBase),
      descontoTotalItem: Number(desconto)
    }]);

    setProdutoId("");
    setQuantidade("1");
    setPrecoBase("");
    setDesconto("0");
  };

  const removerItem = (idx: number) => {
    setItens(itens.filter((_, i) => i !== idx));
  };

  const finalizarVenda = () => {
    if (itens.length === 0) return;
    mutationVenda.mutate({ itens });
  };

  const totalVenda = itens.reduce((acc, item) => acc + ((item.quantidade * item.precoBasePraticado) - item.descontoTotalItem), 0);

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Frente de Caixa (PDV)</h1>
        <p className="text-muted-foreground mt-1">Registre as vendas do dia de forma atômica.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="md:col-span-1 border-primary/20 bg-muted/30">
          <CardContent className="p-6">
            <h2 className="font-semibold text-lg mb-4 flex items-center gap-2"><ShoppingCart className="w-5 h-5"/> Adicionar Item</h2>
            <form onSubmit={adicionarItem} className="space-y-4">
              <div className="space-y-2">
                <Label>Produto</Label>
                <select required className="flex h-10 w-full rounded-md border border-input bg-background px-3" value={produtoId} onChange={(e) => setProdutoId(e.target.value)}>
                  <option value="">Selecione...</option>
                  {produtos?.map((p: any) => <option key={p.id} value={p.id}>{p.nome} (Estoque: {p.saldoEstoque})</option>)}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Quantidade</Label>
                  <Input required type="number" min="1" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} />
                </div>
                <div className="space-y-2">
                  <Label>Preço Unid. (R$)</Label>
                  <Input required type="number" step="0.01" value={precoBase} onChange={(e) => setPrecoBase(e.target.value)} />
                </div>
              </div>
              <div className="space-y-2">
                <Label>Desconto Total na Linha (R$)</Label>
                <Input required type="number" step="0.01" value={desconto} onChange={(e) => setDesconto(e.target.value)} />
              </div>
              <Button type="submit" className="w-full">Incluir no Carrinho</Button>
            </form>
          </CardContent>
        </Card>

        <Card className="md:col-span-2">
          <CardContent className="p-6 flex flex-col h-full">
            <h2 className="font-semibold text-lg mb-4">Itens da Venda</h2>
            <div className="flex-1 overflow-auto bg-background rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Produto</TableHead>
                    <TableHead className="text-right">Qtd</TableHead>
                    <TableHead className="text-right">Preço Un.</TableHead>
                    <TableHead className="text-right">Desc.</TableHead>
                    <TableHead className="text-right">Total Linha</TableHead>
                    <TableHead></TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {itens.map((item, idx) => (
                    <TableRow key={idx}>
                      <TableCell className="font-medium">{item.nome}</TableCell>
                      <TableCell className="text-right">{item.quantidade}</TableCell>
                      <TableCell className="text-right">{formatterBRL.format(item.precoBasePraticado)}</TableCell>
                      <TableCell className="text-right text-red-500">{formatterBRL.format(item.descontoTotalItem)}</TableCell>
                      <TableCell className="text-right font-semibold">
                        {formatterBRL.format((item.quantidade * item.precoBasePraticado) - item.descontoTotalItem)}
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="icon" onClick={() => removerItem(idx)}>
                          <Trash2 className="w-4 h-4 text-red-500" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                  {itens.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">Carrinho vazio.</TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </div>
            
            <div className="mt-6 flex items-center justify-between border-t pt-4">
              <div className="text-sm text-muted-foreground">Total de itens: {itens.length}</div>
              <div className="flex items-center gap-6">
                <div className="text-right">
                  <div className="text-sm text-muted-foreground">Total da Venda</div>
                  <div className="text-3xl font-bold text-primary">{formatterBRL.format(totalVenda)}</div>
                </div>
                <Button size="lg" className="bg-emerald-600 hover:bg-emerald-700 h-14 px-8 text-lg" disabled={itens.length === 0 || mutationVenda.isPending} onClick={finalizarVenda}>
                  {mutationVenda.isPending ? "Processando..." : "Finalizar Venda"}
                </Button>
              </div>
            </div>
            {sucesso && (
              <div className="mt-4 p-4 bg-emerald-100 text-emerald-800 rounded-md flex items-center gap-2">
                <CheckCircle className="w-5 h-5"/> Venda realizada com sucesso! Saldo e custo médio foram atualizados.
              </div>
            )}
            {mutationVenda.isError && (
              <div className="mt-4 p-4 bg-red-100 text-red-800 rounded-md">
                Erro ao realizar venda. Verifique se há saldo suficiente no estoque.
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
