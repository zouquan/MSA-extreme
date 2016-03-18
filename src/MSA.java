import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author ��Ȩ
 * @version 2016��2��27��
 */

public class MSA {
	String filepath = ""; // ��¼�ļ�����
	String Pi[]; // ��¼ÿһ������
	String Piname[]; // ��¼ÿһ�����е�����
	int Spaceevery[][]; // ����������зֱ����������бȶ�ʱ���ӵĿո��λ��
	int Spaceother[][]; // ��������������������бȶ�ʱ���ӵĿո��λ��
	int n; // ������и���
	int center; // ����������б��
	int spacescore = -1, matchscore = 0, mismatchscore = -1; // ###########����ƥ�䣬��ƥ��Ϳո�ķ���###############
	int Name[][][][]; // �������꣬��¼ƥ�������;��ά���飬��һλ��Pi[i],�ڶ�λ��pi[j]����¼�������еı�ţ���3ά��3��Name[i][j][0][k]��¼����Pi[i]�е���ʼλ��
						// Name[i][j][1][k] ��¼����Pi[j]�е���ʼλ��
						// Name[i][j][2][k] ��¼����ƥ��ĳ���

	public MSA(String Path) {
		filepath = Path;
	}

	// �������и���
	public int countnum() {
		int num = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String s;
			while (br.ready()) {
				s = br.readLine();
				if (s.charAt(0) == '>')
					num++;
			}
			br.close();
		} catch (Exception ex) {
		}
		return (num);
	}

	// ������һ�ζ���������
	public void input() {
		Pi = new String[n];
		Piname = new String[n];
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));

			String BR = br.readLine();
			while (br.ready()) {

				if (BR.length() != 0 && BR.charAt(0) == '>') {
					Piname[i] = BR;
					Pi[i] = "";
					while (br.ready() && (BR = br.readLine()).charAt(0) != '>') {
						Pi[i] += BR;
					}
					Pi[i] = format(Pi[i]);
					i++;
				} else
					BR = br.readLine();
			}

			br.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(0);
		}
	}

	// �ҳ�Num[][]�����к�������һ��
	public int findNumMax(int Num[][]) {
		int Numsum[] = new int[n];
		for (int i = 0; i < n; i++) {
			Numsum[i] = 0;
			for (int j = 0; j < n; j++)
				Numsum[i] = Numsum[i] + Num[i][j];
		}
		int tmpcenter = 0;
		for (int i = 1; i < n; i++) {
			if (Numsum[i] > Numsum[tmpcenter])
				tmpcenter = i;
		}
		return (tmpcenter);
	}

	/**
	 * ��String��ʽ��,ɾ���Ƿ��ַ�(ֻ����agctn,�����ַ�ȫ���滻��n),ȫ��ת����Сд,uȫ������t
	 * 
	 * @param s
	 * @return
	 */
	public String format(String s) {
		s = s.toLowerCase();
		s = s.replace('u', 't');
		StringBuffer sb = new StringBuffer(s);

		for (int i = 0; i < sb.length(); i++) {
			switch (sb.charAt(i)) {
			case 'a':
				break;
			case 'c':
				break;
			case 'g':
				break;
			case 't':
				break;
			case 'n':
				break;
			default:
				sb = sb.replace(i, i + 1, "n");
			}
		}
		return (sb.toString());
	}

	// �ҳ���������
	public int findCenterSeq() {

		Name = new int[n][n][3][]; // ������ų��ֵ����֣�Name[i][j][0][k]��Name[i][j][1][k]��ʾ��Pi[i]���еĵ�Name[i][j][0][k]��Ƭ�γ�����Pi[j]���е�Name[i][j][1][k]λ����



			SuffixTree st1 = new SuffixTree();
			st1.build(Pi[0] + "$");
			

			for (int j = 1; j < n; j++) {

				int index = 0;

				int totalmatch = 0;
				ArrayList<Integer> result = new ArrayList();
				while (index < Pi[j].length()) {
					int[] a = st1.selectPrefixForAlignment(Pi[j], index);
					if (a[1] > Math.abs(a[0] - index)) {
						result.add(a[0]);
						result.add(index);
						result.add(a[1]);
						index += a[1];
						totalmatch += a[1];
					} else if (a[1] > 0)
						index += a[1];
					else
						index++;
				}
	
				int[][] tmp = new int[3][result.size() / 3];
				int k = 0;
				while (k < result.size()) {
					tmp[0][k / 3] = result.get(k);
					k++;
					tmp[1][k / 3] = result.get(k);
					k++;
					tmp[2][k / 3] = result.get(k);
					k++;
				}
				Name[0][j] = tmp;

			}

		

		return (0);
	}

	// �ڶ�̬�滮�м���������
	public int[][] computeScoreMatrixForDynamicProgram(String stri, String strC) {
		int len1 = stri.length() + 1;
		int len2 = strC.length() + 1;
		int M[][] = new int[len1][len2]; // ���嶯̬�滮����
		// ---��ʼ����̬�滮����-----------
		int p, q;

		for (p = 0; p < len1; p++)
			M[p][0] = spacescore * p;
		for (q = 0; q < len2; q++)
			M[0][q] = spacescore * q;
		// ---��ʼ������----------
		// ----��������ֵ------------
		for (p = 1; p < len1; p++)
			for (q = 1; q < len2; q++) {// M[p][q]=max(M[p-1][q]-1,M[p][q-1]-1,M[p-1][q-1]+h)
				int h;
				if (stri.charAt(p - 1) == strC.charAt(q - 1))
					h = matchscore;
				else
					h = mismatchscore;
				M[p][q] = Math.max(M[p - 1][q - 1] + h, Math.max(M[p - 1][q] + spacescore, M[p][q - 1] + spacescore));
			}
		return (M);
	}

	// �ڶ�̬�滮�л���
	public void traceBackForDynamicProgram(int[][] M, int p, int q, int i, int k1, int k2) {
		while (p > 0 && q > 0) {
			if (M[p][q] == M[p][q - 1] + spacescore) {
				Spaceother[i][p + k1]++;
				q--;
			} else if (M[p][q] == M[p - 1][q] + spacescore) {
				Spaceevery[i][q + k2]++;
				p--;
			} else {
				p--;
				q--;
			}
		}
		if (p == 0)
			while (q > 0) {
				Spaceother[i][k1]++;
				q--;
			}
		if (q == 0)
			while (p > 0) {
				Spaceevery[i][k2]++;
				p--;
			}
	}

	// �ȶ�Pi[i]�������������غϵ�ǰ��
	public void prealign(int i) {

		String strC = Pi[center].substring(0, Name[center][i][0][0]);
		String stri = Pi[i].substring(0, Name[center][i][1][0]);
		int M[][] = new int[stri.length() + 1][strC.length() + 1]; // ���嶯̬�滮����
		M = computeScoreMatrixForDynamicProgram(stri, strC);// ��̬�滮����������
		traceBackForDynamicProgram(M, stri.length(), strC.length(), i, 0, 0);// ���ݣ����Ŀո�����

	}

	// �ȶ�Pi[i]�������������غϵ��м䲿��
	public void midalign(int i, int j) {
		
		int lamda = Math.max(Name[center][i][1][j - 1] + Name[center][i][2][j - 1] - Name[center][i][1][j],
				Name[center][i][0][j - 1] + Name[center][i][2][j - 1] - Name[center][i][0][j]);
		// lamda��Ϊ�˷�ֹǰ��������ȫƥ���и��ǣ��Ѹ��ǲ���ɾ��
		if (lamda > 0) {
			Name[center][i][0][j] += lamda;
			Name[center][i][1][j] += lamda;
			Name[center][i][2][j] -= lamda;
		}
		if (Name[center][i][2][j] < 0)
			System.out.println("�˴��д��󣡣�����");

		String strC = Pi[center].substring(Name[center][i][0][j - 1] + Name[center][i][2][j - 1],
				Name[center][i][0][j]);// �˴���©�������Name[center][i][0][0]=0���ᱧ��
	
		String stri = Pi[i].substring(Name[center][i][1][j - 1] + Name[center][i][2][j - 1], Name[center][i][1][j]);

		int M[][] = new int[stri.length() + 1][strC.length() + 1]; // ���嶯̬�滮����
		M = computeScoreMatrixForDynamicProgram(stri, strC);// ��̬�滮����������
		traceBackForDynamicProgram(M, stri.length(), strC.length(), i,
				Name[center][i][1][j - 1] + Name[center][i][2][j - 1],
				Name[center][i][0][j - 1] + Name[center][i][2][j - 1]);

	}

	// �ȶ�Pi[i]�������������غϵĺ��
	public void postalign(int i) {

		int j = Name[center][i][0].length;
		if (j > 0) {
			int cstart = Name[center][i][0][j - 1] + Name[center][i][2][j - 1];
			if (cstart > Pi[center].length())
				cstart--;// �����ˣ�����׺��ʱ���˸�$
			int istart = Name[center][i][1][j - 1] + Name[center][i][2][j - 1];
			if (istart > Pi[i].length())
				istart--;

			
			String strC = Pi[center].substring(cstart);

			String stri = Pi[i].substring(Name[center][i][1][j - 1] + Name[center][i][2][j - 1]);

			int M[][] = new int[stri.length() + 1][strC.length() + 1]; // ���嶯̬�滮����

			M = computeScoreMatrixForDynamicProgram(stri, strC);// ��̬�滮����������

			traceBackForDynamicProgram(M, stri.length(), strC.length(), i,istart,cstart);

		} else {
			String strC = Pi[center];
			String stri = Pi[i];
			int M[][] = new int[stri.length() + 1][strC.length() + 1]; // ���嶯̬�滮����
			M = computeScoreMatrixForDynamicProgram(stri, strC);// ��̬�滮����������
			traceBackForDynamicProgram(M, stri.length(), strC.length(), i, 0, 0);
		}
	}

	// ��Pi[i]���������н��бȶ�
	public void pairwiseAlignForImproved(int i) {
		prealign(i);

		for (int j = 1; j < Name[center][i][0].length; j++) {

			midalign(i, j);
		}

		postalign(i);

	}

	public void pairwiseAlignForOld(int i) {
		int M[][] = new int[Pi[i].length() + 1][Pi[center].length() + 1];
		M = computeScoreMatrixForDynamicProgram(Pi[i], Pi[center]);// ��̬�滮����������
		traceBackForDynamicProgram(M, Pi[i].length(), Pi[center].length(), i, 0, 0);
	}

	public int[] combine() {
		int Space[] = new int[Pi[center].length() + 1];// ������������¼��P[center]�����ս��������϶�����ո�ĸ���
		int i, j;
		for (i = 0; i < Pi[center].length() + 1; i++) {
			int max = 0;
			for (j = 0; j < n; j++)
				if (Spaceevery[j][i] > max)
					max = Spaceevery[j][i];
			Space[i] = max;
		}
		return (Space);
	}

	// �������������������������е���󳤶�
	public int computeMaxLength(int center) {
		int maxlength = 0;
		for (int i = 0; i < n; i++) {
			if (i == center)
				continue;
			if (Pi[i].length() > maxlength)
				maxlength = Pi[i].length();
		}
		return (maxlength);
	}

	// �����бȶԲ���
	public void mutipleSequenceAlign(boolean improved) {
		Spaceevery = new int[n][Pi[center].length() + 1];// ����������зֱ����������бȶ�ʱ���ӵĿո��λ��
		Spaceother = new int[n][computeMaxLength(center) + 1];// ��������������������бȶ�ʱ���ӵĿո��λ��
		for (int i = 0; i < n; i++) {

			if (i == center)
				continue;
			if (improved == true)
				pairwiseAlignForImproved(i);
			else if (improved == false)
				pairwiseAlignForOld(i);
		}
		int Space[] = new int[Pi[center].length() + 1];// ������������¼��P[center]�����ս��������϶�����ո�ĸ���
		Space = combine();
		output(Space);
	}

	public void output(int[] Space) {
		int i, j;
		// ---------�����������----------
		String PiAlign[] = new String[n];
		PiAlign[center] = "";
		for (i = 0; i < Pi[center].length(); i++) {
			for (j = 0; j < Space[i]; j++)
				PiAlign[center] = PiAlign[center].concat("-");
			PiAlign[center] = PiAlign[center].concat(Pi[center].substring(i, i + 1));
		}
		for (j = 0; j < Space[Pi[center].length()]; j++)
			PiAlign[center] = PiAlign[center].concat("-");
		// --------��������������------
		// ---------�����������-------
		for (i = 0; i < n; i++) {
			if (i == center)
				continue;
			// ----������������бȶԺ��P[i],��ΪPi-----
			PiAlign[i] = "";
			for (j = 0; j < Pi[i].length(); j++) {
				String kong = "";
				for (int k = 0; k < Spaceother[i][j]; k++)
					kong = kong.concat("-");
				PiAlign[i] = PiAlign[i].concat(kong).concat(Pi[i].substring(j, j + 1));
			}
			String kong = "";
			for (j = 0; j < Spaceother[i][Pi[i].length()]; j++)
				kong = kong.concat("-");
			PiAlign[i] = PiAlign[i].concat(kong);

			// ---Pi�������---------
			// ----�����������----
			int Cha[] = new int[Pi[center].length() + 1];
			int position = 0; // ������¼�������ո��λ��
			for (j = 0; j < Pi[center].length() + 1; j++) {
				Cha[j] = 0;
				if (Space[j] - Spaceevery[i][j] > 0)
					Cha[j] = Space[j] - Spaceevery[i][j];
				// ----��������������---
				// ----�������ո�----
				position = position + Spaceevery[i][j];
				if (Cha[j] > 0) { // ��λ��position������Cha[j]���ո�
					kong = "";
					for (int k = 0; k < Cha[j]; k++)
						kong = kong.concat("-");
					PiAlign[i] = PiAlign[i].substring(0, position).concat(kong).concat(PiAlign[i].substring(position));
				}
				position = position + Cha[j] + 1;
				// ----����ո��������--
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
			for (i = 0; i < n; i++) {
				// System.out.println(Piname[i]);
				bw.write(Piname[i]);
				bw.newLine();
				bw.flush();
				// System.out.println(PiAlign[i]);
				bw.write(PiAlign[i]);
				bw.newLine();
				bw.flush();

			}
			bw.close();
		} catch (Exception ex) {

		}
		// ---------��������������-----
		// ---------�������--------------

	}

	// ����ԭʼ�Ǳȶ���sim�����ֵ
	public int[][] computesim() {
		int[][] sim = new int[n][n];
		int i, j;
		// ����������
		for (i = 0; i < n; i++)
			for (j = i + 1; j < n; j++) {
				int M[][] = new int[Pi[i].length() + 1][Pi[j].length() + 1];
				M = computeScoreMatrixForDynamicProgram(Pi[i], Pi[j]);// ��̬�滮����������
				sim[i][j] = M[Pi[i].length()][Pi[j].length()];
			}
		// -----����sim[][]��������
		for (i = 0; i < n; i++)
			sim[i][i] = 0;
		for (i = 1; i < n; i++)
			for (j = 0; j < i; j++)
				sim[i][j] = sim[j][i];
		return (sim);
	}

	// �Ľ����Ǳȶ��㷨
	public void runforimproved() {
		long t1 = System.currentTimeMillis();
		n = countnum();// ��¼���еĸ���
		input();
		center = findCenterSeq();

		long t2 = System.currentTimeMillis();
	      System.out.print("Ѱ���������г�������ʱ����");
	      System.out.print(t2 - t1);
	      System.out.println("����");
		mutipleSequenceAlign(true);
		
		long t3 = System.currentTimeMillis();
	      System.out.print("�����������ʱ����");
	      System.out.print(t3 - t2);
	      System.out.println("����");
	}

	// ԭʼ���Ǳȶ��㷨
	public void runforold() {
		n = countnum();// ��¼���еĸ���
		input();
		center = findNumMax(computesim());
		System.out.println("���������ǵ�" + center + "����");
		mutipleSequenceAlign(false);
	}

}
